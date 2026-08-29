package org.jeecg.modules.job.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.cms.entity.*;
import org.jeecg.modules.job.cms.service.*;
import org.jeecg.modules.job.job.service.IJobOrderService;
import org.jeecg.modules.job.job.service.IJobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 内容管理模块
 * @Author: qingkonglan
 * @Date:   2024-08-22
 * @Version: V1.0
 */
@RestController
@RequestMapping("/api/cms")
@Api(tags="移动端内容管理模块")
@Slf4j
public class CmsController {
	@Autowired
	private org.jeecg.modules.job.cms.service.ICmsArticlesService articlesService;
	@Autowired
	private org.jeecg.modules.job.cms.service.ICmsCategoryService categoryService;
	@Autowired
	private org.jeecg.modules.job.cms.service.ICmsFeedbackService feedbackService;
	@Autowired
	private org.jeecg.modules.job.cms.service.ICmsNoticeService noticeService;
	@Autowired
	private ICmsAdService adService;
	@Autowired
	private ICmsNewsService newsService;
	@Autowired
	private ICmsTypeService typeService;
	@Autowired
	private ICmsAboutUsService aboutUsService;
	@Autowired
	private ICmsContactUsService contactUsService;
	@Resource
	private ICmsNoticeReadService readService;
	@Resource
	private IJobPostService postService;


	/**
	 * 内容查询接口（查询详情）
	 *
	 * @param categoryCode
	 * @return
	 */
	@ApiOperation("内容查询接口")
	@GetMapping(value = "/getArticle")
	public Result<Object> getArticle(@RequestParam("categoryCode") String categoryCode) {
		if(oConvertUtils.isEmpty(categoryCode)){
			return Result.error("分类编号不能为空");
		}
		try {
			String ids= categoryService.queryChildIds(categoryCode);
			QueryWrapper queryWrapper=new QueryWrapper();
			List<String> newIds= Arrays.asList(ids.split(","));
			queryWrapper.in("category_id",newIds);
			queryWrapper.orderByAsc("sort");
			List<CmsArticles> list=articlesService.list(queryWrapper);
			if(list.size()>0){
				return Result.ok(list.get(0));
			}
			return Result.ok();
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 内容列表查询接口（无详情，用于列表展示）
	 *
	 * @param categoryCode
	 * @return
	 */
	@ApiOperation("内容列表查询接口")
	@GetMapping(value = "/getArticleList")
	public Result<Object> getArticleList(@RequestParam("categoryCode") String categoryCode) {
		if(oConvertUtils.isEmpty(categoryCode)){
			return Result.error("分类编号不能为空");
		}
		try {
			String ids= categoryService.queryChildIds(categoryCode);
			QueryWrapper queryWrapper=new QueryWrapper();
			List<String> newIds= Arrays.asList(ids.split(","));
			queryWrapper.in("category_id",newIds);
			queryWrapper.orderByAsc("sort");
			queryWrapper.select("id,title,category_id,avatar,banner");
			List<CmsArticles> list=articlesService.list(queryWrapper);
			return Result.ok(list);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 子分类查询接口
	 *
	 * @param categoryCode
	 * @return
	 */
	@ApiOperation("子分类查询接口")
	@GetMapping(value = "/getCategoryChilds")
	public Result<Object> getCategoryList(@RequestParam("categoryCode") String categoryCode) {
		if(oConvertUtils.isEmpty(categoryCode)){
			return Result.error("分类编号不能为空");
		}
		try {
			List<CmsCategory> list= categoryService.getCategoryChilds(categoryCode);
			return Result.ok(list);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 添加意见反馈
	 *
	 * @param cmsFeedback
	 * @return
	 */
	@ApiOperation("添加意见反馈")
	@PostMapping(value = "/addFeedback")
	public Result<Object> addFeedback(@RequestBody org.jeecg.modules.job.cms.entity.CmsFeedback cmsFeedback) {
		if(oConvertUtils.isEmpty(cmsFeedback.getContent())){
			return Result.error("反馈内容不能为空");
		}
		try {
			boolean result=feedbackService.save(cmsFeedback);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询公告列表
	 *
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="查询公告列表")
	@GetMapping(value = "/getNoticeList")
	public Result<IPage<CmsNotice>> getNoticeList( @RequestParam(name="userId", required = false) String userId,
												   @RequestParam(name="plat", required = false) String plat,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="type", defaultValue="0") Integer type,
												   @RequestParam(name="roleCode", required = false) String roleCode,
												   @RequestParam(name="pageSize", defaultValue="20") Integer pageSize) {
		QueryWrapper<CmsNotice> queryWrapper=new QueryWrapper<>(new CmsNotice().setStatus(1));
		queryWrapper.eq("type",type);
		if(type!=0){
			queryWrapper.eq("user_id",userId);
		}
		// 订单动态必须按当前身份过滤：同一用户可兼老板/工人，不能串台
		if (type != null && type == 1 && oConvertUtils.isNotEmpty(roleCode)) {
			queryWrapper.eq("role_code", roleCode);
		}
		queryWrapper.orderByDesc("set_top");
		queryWrapper.orderByDesc("create_time");
		Page<CmsNotice> page = new Page<>(pageNo, pageSize);
		IPage<CmsNotice> pageList = noticeService.page(page,queryWrapper);
		if (oConvertUtils.isNotEmpty(userId)){
			pageList.getRecords().forEach(item->{
				//查询是否已读
				long count=readService.count(new QueryWrapper<>(new CmsNoticeRead().setUserId(userId).setNoticeId(item.getId())));
				if (count > 0){
					item.setIfRead(true);
				}else{
					//添加已读记录
					readService.save(new CmsNoticeRead().setNoticeId(item.getId()).setUserId(userId));
				}
				if (type==1){//订单动态
					try {
						org.jeecg.modules.job.job.entity.JobPost post = postService.getById(item.getDataId());
						if (post != null) {
							item.setPostTitle(post.getTitle());
						}
					} catch (Exception e) {
						log.warn("填充订单通知岗位标题失败 noticeId={}", item.getId(), e);
					}
				}else if(type==2 && item.getExcerpt()!=null){//送达凭证

				}
			});
		}
		return Result.OK(pageList);
	}


	/**
	 * 查询未读通知数量
	 *
	 * @param userId
	 * @return
	 */
	@ApiOperation("查询未读通知数量")
	@GetMapping(value = "/getUnReadTotalCount")
	public Result<Object> getUnReadTotalCount(@RequestParam(name="userId", required = false) String userId,
											  @RequestParam(name="roleCode", required = false) String roleCode) {
		try {
			Map<String,Object> map=new HashMap<>();
			CmsNotice notice1 = null,notice2 = null;
			int publicCount=noticeService.getUnReadCount(null,null,0);
			int orderCount = oConvertUtils.isNotEmpty(roleCode)
					? noticeService.getUnReadCount(roleCode, userId, 1) : 0;
			int privateCount=noticeService.getUnReadCount(null,userId,2);
			int financeCount=noticeService.getUnReadCount(null,userId,3);
			int violationCount=noticeService.getUnReadCount(null,userId,4);
			map.put("totalCount",publicCount+orderCount+privateCount+financeCount+violationCount);
			//查询最近的一条通知（订单动态按当前身份隔离）
			QueryWrapper<CmsNotice> recentQw = new QueryWrapper<CmsNotice>().eq("user_id", userId);
			if (oConvertUtils.isNotEmpty(roleCode)) {
				recentQw.and(w -> w.nested(n -> n.eq("type", 1).eq("role_code", roleCode))
						.or().in("type", 2, 3, 4));
			} else {
				recentQw.in("type", 2, 3, 4);
			}
			List<CmsNotice> list2 = noticeService.list(recentQw.orderByDesc("create_time").last("limit 1"));
			if (list2.size()==1){
				notice1=list2.get(0);
			}
			//查询最近的一条系统通知
			List<CmsNotice> list1=noticeService.list(new QueryWrapper<CmsNotice>().eq("if_public",1).orderByDesc("create_time").last("limit 1"));
			if (list1.size()==1){
				notice2=list1.get(0);
			}
			if (notice1==null && notice2==null){
				map.put("desc","暂无通知");
			}else if (notice1==null && notice2!=null){
				map.put("desc",notice2.getTitle());
				map.put("time",notice2.getCreateTime());
			}else if (notice1!=null && notice2==null){
				map.put("desc",notice1.getTitle());
				map.put("time",notice1.getCreateTime());
			}else if (notice1!=null && notice2!=null){
				if (notice1.getCreateTime().compareTo(notice2.getCreateTime())>0){
					map.put("desc",notice1.getTitle());
					map.put("time",notice1.getCreateTime());
				}else{
					map.put("desc",notice2.getTitle());
					map.put("time",notice2.getCreateTime());
				}
			}else{
				map.put("desc","暂无通知");
			}
			return Result.ok(map);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询未读通知数量
	 *
	 * @param userId
	 * @return
	 */
	@ApiOperation("查询未读通知数量")
	@GetMapping(value = "/getUnReadCount")
	public Result<Object> getUnReadCount(@RequestParam(name="userId", required = false) String userId,
										 @RequestParam(name="roleCode", required = false) String roleCode) {
		try {
			Map<String,Object> map=new HashMap<>();
			if (oConvertUtils.isEmpty(userId)){
				int publicCount=noticeService.getUnReadCount(null,null,0);
				map.put("publicCount",publicCount);
			}else{
				int publicCount=noticeService.getUnReadCount(null,null,0);
				// 订单未读按身份隔离；未传 roleCode 时不计订单未读，避免老板/工人串台
				int orderCount = oConvertUtils.isNotEmpty(roleCode)
						? noticeService.getUnReadCount(roleCode, userId, 1) : 0;
				int privateCount=noticeService.getUnReadCount(null,userId,2);
				int financeCount=noticeService.getUnReadCount(null,userId,3);
				int violationCount=noticeService.getUnReadCount(null,userId,4);
				map.put("publicCount",publicCount);
				map.put("orderCount",orderCount);
				map.put("privateCount",privateCount);
				map.put("financeCount",financeCount);
				map.put("violationCount",violationCount);
				//查询最近的一条订单通知（按当前身份）
				if (oConvertUtils.isNotEmpty(roleCode)) {
					List<CmsNotice> list2 = noticeService.list(new QueryWrapper<CmsNotice>()
							.eq("user_id", userId).eq("type", 1).eq("role_code", roleCode)
							.orderByDesc("create_time").last("limit 1"));
					if (list2.size() == 1) {
						map.put("orderDesc", list2.get(0).getTitle());
						map.put("orderTime", list2.get(0).getCreateTime());
					} else {
						map.put("orderDesc", "暂无消息");
					}
				} else {
					map.put("orderDesc", "暂无消息");
				}
				//查询最近的一条私信通知
				List<CmsNotice> list3=noticeService.list(new QueryWrapper<CmsNotice>().eq("user_id",userId).eq("type",2).orderByDesc("create_time").last("limit 1"));
				if (list3.size()==1){
					map.put("privateDesc",list3.get(0).getTitle());
					map.put("privateTime",list3.get(0).getCreateTime());
				}else{
					map.put("privateDesc","暂无消息");
				}
				//查询最近的一条违规记录
				List<CmsNotice> list33=noticeService.list(new QueryWrapper<CmsNotice>().eq("user_id",userId).eq("type",4).orderByDesc("create_time").last("limit 1"));
				if (list33.size()==1){
					map.put("violationDesc",list33.get(0).getTitle());
					map.put("violationTime",list33.get(0).getCreateTime());
				}else{
					map.put("violationDesc","暂无消息");
				}
				//查询最近的一条动账通知
				List<CmsNotice> list4=noticeService.list(new QueryWrapper<CmsNotice>().eq("user_id",userId).eq("type",3).orderByDesc("create_time").last("limit 1"));
				if (list4.size()==1){
					map.put("financeDesc",list4.get(0).getTitle());
					map.put("financeTime",list4.get(0).getCreateTime());
				}else{
					map.put("financeDesc","暂无消息");
				}
			}
			//查询最近的一条系统通知
			List<CmsNotice> list1=noticeService.list(new QueryWrapper<CmsNotice>().eq("if_public",1).orderByDesc("create_time").last("limit 1"));
			if (list1.size()==1){
				map.put("publicDesc",list1.get(0).getTitle());
				map.put("publicTime",list1.get(0).getCreateTime());
			}else{
				map.put("privateDesc","暂无通知");
			}
			return Result.ok(map);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}


	/**
	 * 全部已读
	 * @return
	 */
	@ApiOperation("全部已读")
	@PostMapping(value = "/setAllRead")
	public Result<Object> setAllRead(@RequestBody JSONObject jsonObject) {
		String userId = jsonObject.getString("userId");
		if(oConvertUtils.isEmpty(userId)){
			return Result.error("参数错误");
		}
		try {
			boolean result=noticeService.setAllRead(userId);
			return Result.ok(result);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询通知详情
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("查询通知详情")
	@GetMapping(value = "/getNoticeDetail")
	public Result<Object> getNoticeDetail(@RequestParam("id") String id) {
		try {
			org.jeecg.modules.job.cms.entity.CmsNotice notice=noticeService.getById(id);
			return Result.ok(notice);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询广告列表
	 *
	 * @return
	 */
	@ApiOperation(value="查询广告列表")
	@GetMapping(value = "/getAdList")
	public Result<List<CmsAd>> getAdList(@RequestParam(name="roleCode", defaultValue="member") String roleCode,
										 @RequestParam(name="adPosition", defaultValue="index_banner") String adPosition) {
		List<CmsAd> pageList = adService.list(new QueryWrapper<>(new CmsAd().setStatus(1).setRoleCode(roleCode).setAdPosition(adPosition)).orderByAsc("sort"));
		return Result.OK(pageList);
	}

	/**
	 * 查询新闻列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value="查询新闻列表")
	@GetMapping(value = "/getNewsList")
	public Result<IPage<CmsNews>> getNewsList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											  @RequestParam(name="pageSize", defaultValue="20") Integer pageSize,
											  @RequestParam(name="categoryId", defaultValue="news") String categoryId) {
		Page<CmsNews> page = new Page<>(pageNo, pageSize);
		IPage<CmsNews> pageList = newsService.page(page,new QueryWrapper<>(new CmsNews().setCategoryId(categoryId).setStatus(1)).orderByDesc("create_time"));
		return Result.OK(pageList);
	}


	/**
	 * 查询新闻详情
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("查询新闻详情")
	@GetMapping(value = "/getNewsDetail")
	public Result<Object> getNewsDetail(@RequestParam("id") String id) {
		try {
			CmsNews news=newsService.getById(id);
			return Result.ok(news);
		} catch (Exception e) {
			log.error("操作异常", e);
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 查询首页分类列表
	 *
	 * @return
	 */
	@ApiOperation(value="查询首页分类列表")
	@GetMapping(value = "/getTypeList")
	public Result<List<CmsType>> getTypeList(@RequestParam(name="roleCode", defaultValue="member") String roleCode) {
		List<CmsType> pageList = typeService.list(new QueryWrapper<>(new CmsType().setStatus(1).setRoleCode(roleCode)).orderByAsc("sort"));
		return Result.OK(pageList);
	}


	/**
	 * 关于我们
	 */
	@RequestMapping(value = "/aboutUs/detail",method = RequestMethod.GET)
	@ApiOperation(value = "关于我们", notes = "关于我们")
	public Result aboutUsDetail() {
		List<CmsAboutUs> results=aboutUsService.list(new QueryWrapper<>(new CmsAboutUs()));
		if (results.size()>0){
			return Result.ok(results.get(0));
		}
		return Result.ok();
	}

	/**
	 * 联系我们
	 */
	@RequestMapping(value = "/contactUs/detail",method = RequestMethod.GET)
	@ApiOperation(value = "联系我们", notes = "联系我们")
	public Result contactUs() {
		List<CmsContactUs> results=contactUsService.list(new QueryWrapper<>(new CmsContactUs()));
		if (results.size()>0){
			return Result.ok(results.get(0));
		}
		return Result.ok();
	}


}

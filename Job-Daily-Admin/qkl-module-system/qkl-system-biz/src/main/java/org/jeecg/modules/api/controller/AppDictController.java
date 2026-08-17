package org.jeecg.modules.api.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.security.DictQueryBlackListHandler;
import org.jeecg.modules.system.service.ISysCategoryService;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/api/dict")
@Api(tags="字典查询")
@Slf4j
public class AppDictController {

	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private DictQueryBlackListHandler dictQueryBlackListHandler;
	@Autowired
	private ISysCategoryService sysCategoryService;

	/**
	 * 获取字典数据 【接口签名验证】
	 * @param dictCode 字典code
	 * @param dictCode 表名,文本字段,code字段  | 举例：sys_user,realname,id
	 * @return
	 */
	@ApiOperation("获取字典数据")
	@RequestMapping(value = "/getDictItems", method = RequestMethod.GET)
	public Result<List<DictModel>> getDictItems(@RequestParam("dictCode") String dictCode) {
		log.info(" dictCode : "+ dictCode);
		if(!dictQueryBlackListHandler.isPass(dictCode)){
			return Result.error(dictQueryBlackListHandler.getError());
		}
		try {
			List<DictModel> ls = sysDictService.getDictItems(dictCode);
			if (ls == null) {
				return Result.error("字典Code格式不正确！");
			}
			ls.forEach(item->{
				item.setName(item.getText());
				item.setId(item.getValue());
			});
			return Result.OK(ls);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Result.error("查询失败");
		}
	}


	/**
	 * 分类字典树控件 加载节点
	 * @param pid
	 * @param pcode
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
	public Result<List<TreeSelectModel>> loadDict(@RequestParam(name="pid",required = false) String pid, @RequestParam(name="pcode",required = false) String pcode, @RequestParam(name="condition",required = false) String condition) {
		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		//pid如果传值了 就忽略pcode的作用
		if(oConvertUtils.isEmpty(pid)){
			if(oConvertUtils.isEmpty(pcode)){
				result.setSuccess(false);
				result.setMessage("加载分类字典树参数有误.[null]!");
				return result;
			}else{
				if(ISysCategoryService.ROOT_PID_VALUE.equals(pcode)){
					pid = ISysCategoryService.ROOT_PID_VALUE;
				}else{
					pid = this.sysCategoryService.queryIdByCode(pcode);
				}
				if(oConvertUtils.isEmpty(pid)){
					result.setSuccess(false);
					result.setMessage("加载分类字典树参数有误.[code]!");
					return result;
				}
			}
		}
		Map<String, String> query = null;
		if(oConvertUtils.isNotEmpty(condition)) {
			query = JSON.parseObject(condition, Map.class);
		}
		List<TreeSelectModel> ls = sysCategoryService.queryListByPid(pid,query);
		ls.forEach(item->{
			item.setId(item.getKey());
			item.setName(item.getTitle());
		});
		result.setSuccess(true);
		result.setResult(ls);
		result.setCode(200);
		return result;
	}
}
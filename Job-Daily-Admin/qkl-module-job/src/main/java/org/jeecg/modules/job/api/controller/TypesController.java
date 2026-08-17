package org.jeecg.modules.job.api.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.job.entity.JobPost;
import org.jeecg.modules.job.job.entity.JobTypes;
import org.jeecg.modules.job.job.service.IJobTypesService;
import org.jeecg.modules.job.ums.entity.UmsSign;
import org.jeecg.modules.job.ums.service.IUmsSignService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 工种管理
 */
@RestController
@RequestMapping("/api/types")
@Api(tags="移动端工种管理模块")
@Slf4j
public class TypesController {

    @Resource
    private IJobTypesService typesService;

    /**
     * 查询工种列表
     */
    @RequestMapping(value = "/getTypeList",method = RequestMethod.GET)
    @ApiOperation(value = "查询工种列表", notes = "查询工种列表")
//    @Cacheable(value = BizConstants.TYPES_REDIS_KEY)
    public Result<Object> getPostList(@RequestParam(value = "pid", required = false) String pid,@RequestParam(value = "userId", required = false) String userId) {
        JobTypes param=new JobTypes();
        param.setStatus(1);
        List<JobTypes> list;
        List<JobTypes> child;
        if (oConvertUtils.isNotEmpty(pid)){
            param.setPid(pid);
            list= typesService.list(new QueryWrapper<>(param).orderByAsc("sort"));
        }else{
            //查询父类
            param.setPid("0");
            list= typesService.list(new QueryWrapper<>(param).orderByAsc("sort"));
            //查询之类
            for (int i=0;i<list.size();i++){
                param.setPid(list.get(i).getId());
                child=typesService.list(new QueryWrapper<>(param).orderByAsc("sort"));
                for (int j=0;j<child.size();j++){
                    param.setPid(child.get(j).getId());
                    QueryWrapper queryWrapper=new QueryWrapper<>(param);
                    if (oConvertUtils.isNotEmpty(userId)){
                        queryWrapper.apply("(if_public = 1 or (if_public=0 and user_id ="+userId+"))");
                    }else{
                        queryWrapper.eq("if_public",1);
                    }
                    queryWrapper.orderByAsc("sort");
                    child.get(j).setChild(typesService.list(queryWrapper));
                }
                list.get(i).setChild(child);
            }
        }
        return Result.ok(list);
    }


    @ApiOperation("查询工种列表")
    @GetMapping(value = "/getTypes")
    public Result<Object> getTypes(@RequestParam(value = "pid", defaultValue = "") String pid,@RequestParam(value = "userId", required = false) String userId) {
        try {
            List<JobTypes> list;
            List<JobTypes> child,child2;
            if (oConvertUtils.isNotEmpty(pid)) {
                //查询一级子类2
                list = typesService.list(new LambdaQueryWrapper<JobTypes>().eq(JobTypes::getPid,pid).eq(JobTypes::getStatus,1).orderByAsc(JobTypes::getSort));
                //查询二级子类3
                for (int i=0;i<list.size();i++){
                    LambdaQueryWrapper<JobTypes> queryWrapper=new LambdaQueryWrapper();
                    queryWrapper.eq(JobTypes::getPid,list.get(i).getId());
                    queryWrapper.eq(JobTypes::getStatus,1);
                    if (oConvertUtils.isNotEmpty(userId)){
                        queryWrapper.apply("(if_public = 1 or (if_public=0 and user_id ="+userId+"))");
                    }else{
                        queryWrapper.eq(JobTypes::getIfPublic,1);
                    }
                    queryWrapper.orderByAsc(JobTypes::getSort);
                    child=typesService.list(queryWrapper);
                    list.get(i).setChild(child);
                }
            }else{
                //查询父类1
                list = typesService.list(new LambdaQueryWrapper<JobTypes>().eq(JobTypes::getPid,"0").eq(JobTypes::getStatus,1).orderByAsc(JobTypes::getSort));
                //查询第一个子类2
                if (list.size()>0){
                    child=typesService.list(new LambdaQueryWrapper<JobTypes>().eq(JobTypes::getPid,list.get(0).getId()).eq(JobTypes::getStatus,1).orderByAsc(JobTypes::getSort));
                    list.get(0).setChild(child);
                    //查询二级分类3
                    for (int j=0;j<child.size();j++){
                        LambdaQueryWrapper<JobTypes> queryWrapper=new LambdaQueryWrapper();
                        queryWrapper.eq(JobTypes::getPid,child.get(j).getId());
                        queryWrapper.eq(JobTypes::getStatus,1);
                        if (oConvertUtils.isNotEmpty(userId)){
                            queryWrapper.apply("(if_public = 1 or (if_public=0 and user_id ="+userId+"))");
                        }else{
                            queryWrapper.eq(JobTypes::getIfPublic,1);
                        }
                        queryWrapper.orderByAsc(JobTypes::getSort);
                        child2=typesService.list(queryWrapper);
                        child.get(j).setChild(child2);
                    }
                }
            }
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询工种列表：%s", e.getMessage(), e);
            return Result.error("查询失败："+e.getMessage());
        }
    }

    @ApiOperation("新增工种")
    @PostMapping(value = "/addTypes")
    public Result<Object> addTypes(@RequestBody JobTypes types) {
        try {
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            types.setUserId(user.getId());
            types.setStatus(1);
            types.setIfHot("0");
            types.setSort(999);
            JobTypes jobTypes=typesService.addJobTypes(types);
            return Result.ok(jobTypes);
        } catch (Exception e) {
            log.error("新增招工信息：%s", e.getMessage(), e);
            return Result.error("操作失败："+e.getMessage());
        }
    }


}
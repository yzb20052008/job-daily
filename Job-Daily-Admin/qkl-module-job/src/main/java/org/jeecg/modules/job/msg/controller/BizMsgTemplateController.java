package org.jeecg.modules.job.msg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.msg.entity.BizMsgTemplate;
import org.jeecg.modules.job.msg.service.IBizMsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 业务消息模板管理
 */
@Api(tags = "业务消息模板")
@RestController
@RequestMapping("/msg/bizMsgTemplate")
@Slf4j
public class BizMsgTemplateController extends JeecgController<BizMsgTemplate, IBizMsgTemplateService> {

    @Autowired
    private IBizMsgTemplateService bizMsgTemplateService;

    @ApiOperation("分页列表")
    @GetMapping("/list")
    public Result<IPage<BizMsgTemplate>> list(BizMsgTemplate query,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<BizMsgTemplate> qw = QueryGenerator.initQueryWrapper(query, req.getParameterMap());
        IPage<BizMsgTemplate> page = bizMsgTemplateService.page(new Page<>(pageNo, pageSize), qw);
        return Result.OK(page);
    }

    @AutoLog(value = "消息模板-新增")
    @ApiOperation("新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody BizMsgTemplate tpl) {
        if (oConvertUtils.isEmpty(tpl.getTemplateCode()) || oConvertUtils.isEmpty(tpl.getChannel())) {
            return Result.error("模板编码与通道不能为空");
        }
        long cnt = bizMsgTemplateService.count(new QueryWrapper<BizMsgTemplate>()
                .eq("template_code", tpl.getTemplateCode().trim()));
        if (cnt > 0) {
            return Result.error("模板编码已存在：" + tpl.getTemplateCode());
        }
        if ("wx".equals(tpl.getChannel()) && oConvertUtils.isEmpty(tpl.getWxTemplateId())) {
            return Result.error("微信通道必须填写微信模板ID");
        }
        tpl.setTemplateCode(tpl.getTemplateCode().trim());
        if (oConvertUtils.isEmpty(tpl.getStatus())) {
            tpl.setStatus("1");
        }
        bizMsgTemplateService.save(tpl);
        return Result.OK("添加成功");
    }

    @AutoLog(value = "消息模板-编辑")
    @ApiOperation("编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody BizMsgTemplate tpl) {
        if (oConvertUtils.isEmpty(tpl.getId())) {
            return Result.error("参数错误");
        }
        if ("wx".equals(tpl.getChannel()) && oConvertUtils.isEmpty(tpl.getWxTemplateId())) {
            return Result.error("微信通道必须填写微信模板ID");
        }
        // 编码变更时校验唯一
        if (oConvertUtils.isNotEmpty(tpl.getTemplateCode())) {
            long cnt = bizMsgTemplateService.count(new QueryWrapper<BizMsgTemplate>()
                    .eq("template_code", tpl.getTemplateCode().trim())
                    .ne("id", tpl.getId()));
            if (cnt > 0) {
                return Result.error("模板编码已存在：" + tpl.getTemplateCode());
            }
            tpl.setTemplateCode(tpl.getTemplateCode().trim());
        }
        bizMsgTemplateService.updateById(tpl);
        return Result.OK("编辑成功");
    }

    @AutoLog(value = "消息模板-删除")
    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam String id) {
        if (oConvertUtils.isEmpty(id)) {
            return Result.error("参数错误");
        }
        bizMsgTemplateService.removeById(id);
        return Result.OK("删除成功");
    }

    @AutoLog(value = "消息模板-批量删除")
    @ApiOperation("批量删除")
    @DeleteMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        if (oConvertUtils.isEmpty(ids)) {
            return Result.error("参数错误");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        bizMsgTemplateService.removeByIds(idList);
        return Result.OK("批量删除成功!");
    }

    @AutoLog(value = "消息模板-批量启停")
    @ApiOperation("批量启停")
    @PostMapping("/updateStatusBatch")
    public Result<String> updateStatusBatch(@RequestBody Map<String, String> body) {
        String ids = body == null ? null : body.get("ids");
        String status = body == null ? null : body.get("status");
        if (oConvertUtils.isEmpty(ids) || oConvertUtils.isEmpty(status)) {
            return Result.error("参数错误");
        }
        if (!"0".equals(status) && !"1".equals(status)) {
            return Result.error("状态仅支持 0/1");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        for (String id : idList) {
            if (oConvertUtils.isEmpty(id)) {
                continue;
            }
            bizMsgTemplateService.updateById(new BizMsgTemplate().setId(id).setStatus(status));
        }
        return Result.OK("1".equals(status) ? "已批量启用" : "已批量停用");
    }

    @ApiOperation("按ID查询")
    @GetMapping("/queryById")
    public Result<BizMsgTemplate> queryById(@RequestParam String id) {
        return Result.OK(bizMsgTemplateService.getById(id));
    }
}

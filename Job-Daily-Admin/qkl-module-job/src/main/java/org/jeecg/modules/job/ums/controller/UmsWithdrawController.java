package org.jeecg.modules.job.ums.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.job.constant.BizConstants;
import org.jeecg.modules.job.ums.entity.UmsWithdraw;
import org.jeecg.modules.job.ums.service.IUmsWithdrawService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 用户提现
 * @Author: qingkonglan
 * @Date:   2023-03-30
 * @Version: V1.0
 */
@Api(tags="用户提现")
@RestController
@RequestMapping("/ums/umsWithdraw")
@Slf4j
public class UmsWithdrawController extends JeecgController<UmsWithdraw, IUmsWithdrawService> {
	@Autowired
	private IUmsWithdrawService umsWithdrawService;
	
	/**
	 * 分页列表查询
	 */
	@ApiOperation(value="用户提现-分页列表查询", notes="用户提现-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UmsWithdraw>> queryPageList(UmsWithdraw umsWithdraw,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Page<UmsWithdraw> page = new Page<UmsWithdraw>(pageNo, pageSize);
		IPage<UmsWithdraw> pageList = umsWithdrawService.getWithdrawPageList(page, umsWithdraw);
		return Result.OK(pageList);
	}
	
	/**
	 * 禁止后台直接录入提现单（须走移动端申请以冻结余额）
	 */
	@AutoLog(value = "用户提现-添加(已禁用)")
	@ApiOperation(value="用户提现-添加", notes="已禁用，请通过移动端提现申请")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody UmsWithdraw umsWithdraw) {
		return Result.error("禁止直接添加提现单，请通过用户端提现申请");
	}
	
	/**
	 * 禁止随意改资金字段，审核请走 updateStatus
	 */
	@AutoLog(value = "用户提现-编辑(已禁用)")
	@ApiOperation(value="用户提现-编辑", notes="已禁用，请通过审核/查单接口")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody UmsWithdraw umsWithdraw) {
		return Result.error("禁止直接编辑提现单，请使用审核或查单回写");
	}


	 /**
	  * 审核：通过后事务外发起微信转账
	  */
	 @AutoLog(value = "用户提现-更新状态")
	 @ApiOperation(value="用户提现-更新状态", notes="用户提现-更新状态")
	 @RequestMapping(value = "/updateStatus", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> updateStatus(@RequestBody UmsWithdraw umsWithdraw) {
	 	try{
			if (oConvertUtils.isEmpty(umsWithdraw.getId()) || oConvertUtils.isEmpty(umsWithdraw.getWithdrawStatus())){
				return Result.error("参数错误");
			}
			int status = umsWithdraw.getWithdrawStatus();
			umsWithdrawService.updateStatus(umsWithdraw.getId(), status, umsWithdraw.getReason());
			// 审核与转账拆事务：审核已提交后再发起渠道转账
			if (status == BizConstants.WITHDRAW_STATUS_SUCCESS) {
				try {
					umsWithdrawService.initiateTransfer(umsWithdraw.getId());
				} catch (Exception e) {
					log.error("审核通过后发起转账失败 id={}", umsWithdraw.getId(), e);
					return Result.error("审核已通过，但发起转账失败，请使用「查单回写」或稍后重试：" + e.getMessage());
				}
			}
			return Result.OK(status == BizConstants.WITHDRAW_STATUS_SUCCESS
					? "审核通过，已发起转账" : "已拒绝并解冻");
		}catch (Exception e){
			return Result.error("操作失败:"+e.getMessage());
		}
	 }

	/**
	 * 审核通过后重新发起微信转账（发起失败/漏发时补救；内部先查单防重发）
	 */
	@AutoLog(value = "用户提现-重新发起转账")
	@ApiOperation(value = "重新发起转账", notes = "仅审核通过且转账未终态")
	@RequestMapping(value = "/retryTransfer", method = {RequestMethod.PUT, RequestMethod.POST})
	public Result<String> retryTransfer(@RequestBody UmsWithdraw umsWithdraw) {
		try {
			if (oConvertUtils.isEmpty(umsWithdraw.getId())) {
				return Result.error("参数错误");
			}
			umsWithdrawService.initiateTransfer(umsWithdraw.getId());
			return Result.OK("已发起转账");
		} catch (Exception e) {
			log.error("重新发起转账失败 id={}", umsWithdraw.getId(), e);
			return Result.error("操作失败:" + e.getMessage());
		}
	}

	/**
	 * 关闭异常提现（无商户单号）并解冻
	 */
	@AutoLog(value = "用户提现-关闭异常单并解冻")
	@ApiOperation(value = "关闭异常提现并解冻", notes = "仅允许：审核通过且无 out_bill_no 的脏数据")
	@RequestMapping(value = "/closeAbnormal", method = {RequestMethod.PUT, RequestMethod.POST})
	public Result<String> closeAbnormal(@RequestBody UmsWithdraw umsWithdraw) {
		try {
			if (oConvertUtils.isEmpty(umsWithdraw.getId())) {
				return Result.error("参数错误");
			}
			umsWithdrawService.closeAbnormalWithdraw(umsWithdraw.getId(), umsWithdraw.getReason());
			return Result.OK("已关闭并解冻");
		} catch (Exception e) {
			return Result.error("操作失败:" + e.getMessage());
		}
	}
	
	/**
	 * 禁止删除提现单（资金流水不可物理删）
	 */
	@AutoLog(value = "用户提现-通过id删除(已禁用)")
	@ApiOperation(value="用户提现-通过id删除", notes="已禁用")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		return Result.error("禁止删除提现单");
	}
	
	/**
	 * 禁止批量删除
	 */
	@AutoLog(value = "用户提现-批量删除(已禁用)")
	@ApiOperation(value="用户提现-批量删除", notes="已禁用")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		return Result.error("禁止删除提现单");
	}
	
	/**
	 * 通过id查询
	 */
	@ApiOperation(value="用户提现-通过id查询", notes="用户提现-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UmsWithdraw> queryById(@RequestParam(name="id",required=true) String id) {
		UmsWithdraw umsWithdraw = umsWithdrawService.getById(id);
		if(umsWithdraw==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(umsWithdraw);
	}

    /**
    * 导出excel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, UmsWithdraw umsWithdraw) {
        return super.exportXls(request, umsWithdraw, UmsWithdraw.class, "用户提现");
    }

    /**
      * 禁止导入（防绕过冻结语义）
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return Result.error("禁止导入提现单");
    }

}

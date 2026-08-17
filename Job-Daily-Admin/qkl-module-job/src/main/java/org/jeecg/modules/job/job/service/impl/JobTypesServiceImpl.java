package org.jeecg.modules.job.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.job.job.entity.JobTypes;
import org.jeecg.modules.job.job.mapper.JobTypesMapper;
import org.jeecg.modules.job.job.service.IJobTypesService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 工种信息
 * @Author: qingkonglan
 * @Date:   2024-07-30
 * @Version: V1.0
 */
@Service
public class JobTypesServiceImpl extends ServiceImpl<JobTypesMapper, JobTypes> implements IJobTypesService {

	@Override
	public JobTypes addJobTypes(JobTypes jobTypes) {
	   //新增时设置hasChild为0
	    jobTypes.setHasChild(IJobTypesService.NOCHILD);
		if(oConvertUtils.isEmpty(jobTypes.getPid())){
			jobTypes.setPid(IJobTypesService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			JobTypes parent = baseMapper.selectById(jobTypes.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
        //添加层级、工种编码
        if (jobTypes.getIfPublic()==1){
            JobTypes type=this.getTypeCodeAndLevel(jobTypes.getPid());
            jobTypes.setLevel(type.getLevel());
            jobTypes.setTypeCode(type.getTypeCode());
        }
		this.save(jobTypes);
		return jobTypes;
	}

    @Override
    public JobTypes getTypeCodeAndLevel(String pid) {
        JobTypes type=new JobTypes();
        JobTypes pType = null;
        String code = null;
        if ("0".equals(pid)) {//1级
            type.setLevel(1);
        }else{//上一级+1
            pType=this.getById(pid);
            type.setLevel(pType.getLevel()+1);
        }
        //获取code
        List<JobTypes> list=this.list(new QueryWrapper<>(new JobTypes().setPid(pid).setIfPublic(1)).orderByDesc("type_code").last("limit 1"));
        if (list.isEmpty()){
            if (pType!=null){
                code=pType.getTypeCode()+"A01";
            }else{
                code="A01";
            }
        }else{
            JobTypes lastType=list.get(0);
            String lastCode =lastType.getTypeCode();
            String a=lastCode.substring(0,lastCode.length()-2);
            String b=lastCode.substring(lastCode.length()-2);
            Integer c=Integer.parseInt(b)+1;
            code = a+String.format("%02d", c);
        }
        type.setTypeCode(code);
        return type;
    }

    public static void main(String[] args) {
        int number = 5;
        // 使用 String.format 方法
        String formattedNumber1 = String.format("%02d", number);
        System.out.println("使用 String.format 方法格式化后的结果: " + formattedNumber1);

    }

    @Override
	public void updateJobTypes(JobTypes jobTypes) {
		JobTypes entity = this.getById(jobTypes.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = jobTypes.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				jobTypes.setPid(IJobTypesService.ROOT_PID_VALUE);
			}
			if(!IJobTypesService.ROOT_PID_VALUE.equals(jobTypes.getPid())) {
				baseMapper.updateTreeNodeStatus(jobTypes.getPid(), IJobTypesService.HASCHILD);
			}
		}
		baseMapper.updateById(jobTypes);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteJobTypes(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    JobTypes jobTypes = this.getById(idVal);
                    String pidVal = jobTypes.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<JobTypes> dataList = baseMapper.selectList(new QueryWrapper<JobTypes>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
                    boolean flag = (dataList == null || dataList.size() == 0) && !Arrays.asList(idArr).contains(pidVal) && !sb.toString().contains(pidVal);
                    if(flag){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            JobTypes jobTypes = this.getById(id);
            if(jobTypes==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(jobTypes.getPid());
            baseMapper.deleteById(id);
        }
	}
	
	@Override
    public List<JobTypes> queryTreeListNoPage(QueryWrapper<JobTypes> queryWrapper) {
        List<JobTypes> dataList = baseMapper.selectList(queryWrapper);
        List<JobTypes> mapList = new ArrayList<>();
        for(JobTypes data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !IJobTypesService.NOCHILD.equals(pidVal)){
                JobTypes rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }

    @Override
    public List<SelectTreeModel> queryListByCode(String parentCode) {
        String pid = ROOT_PID_VALUE;
        if (oConvertUtils.isNotEmpty(parentCode)) {
            LambdaQueryWrapper<JobTypes> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(JobTypes::getPid, parentCode);
            List<JobTypes> list = baseMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0) {
                throw new JeecgBootException("该编码【" + parentCode + "】不存在，请核实!");
            }
            if (list.size() > 1) {
                throw new JeecgBootException("该编码【" + parentCode + "】存在多个，请核实!");
            }
            pid = list.get(0).getId();
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, null);
    }

    @Override
    public JobTypes getTypeNameCodes(String ids) {
        JobTypes types=new JobTypes();
        List<JobTypes> list=this.list(new QueryWrapper<JobTypes>().apply( " FIND_IN_SET(id,'" + ids+ "') "));
        List<String> nameList = list.stream().map(JobTypes::getName).collect(Collectors.toList());
        List<String> codeList = list.stream().map(JobTypes::getTypeCode).collect(Collectors.toList());
        String names = String.join("、", nameList);
        String codes = String.join(",", codeList);
        types.setTypeCode(codes);
        types.setName(names);
        return types;
    }

    /**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IJobTypesService.ROOT_PID_VALUE.equals(pid)) {
			Long count = baseMapper.selectCount(new QueryWrapper<JobTypes>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IJobTypesService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private JobTypes getTreeRoot(String pidVal){
        JobTypes data =  baseMapper.selectById(pidVal);
        if(data != null && !IJobTypesService.ROOT_PID_VALUE.equals(data.getPid())){
            return this.getTreeRoot(data.getPid());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<JobTypes> dataList = baseMapper.selectList(new QueryWrapper<JobTypes>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(JobTypes tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}

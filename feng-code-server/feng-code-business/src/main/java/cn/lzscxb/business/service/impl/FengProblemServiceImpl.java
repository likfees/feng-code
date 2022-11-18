package cn.lzscxb.business.service.impl;

import java.util.List;
import cn.lzscxb.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.lzscxb.business.mapper.FengProblemMapper;
import cn.lzscxb.domain.entity.FengProblem;
import cn.lzscxb.business.service.IFengProblemService;

/**
 * 题目管理Service业务层处理
 * 
 * @author Likfees
 * @date 2022-11-17
 */
@Service
public class FengProblemServiceImpl implements IFengProblemService 
{
    @Autowired
    private FengProblemMapper fengProblemMapper;

    /**
     * 查询题目管理
     * 
     * @param id 题目管理主键
     * @return 题目管理
     */
    @Override
    public FengProblem selectFengProblemById(Long id)
    {
        return fengProblemMapper.selectFengProblemById(id);
    }

    /**
     * 查询题目管理列表
     * 
     * @param fengProblem 题目管理
     * @return 题目管理
     */
    @Override
    public List<FengProblem> selectFengProblemList(FengProblem fengProblem)
    {
        return fengProblemMapper.selectFengProblemList(fengProblem);
    }

    /**
     * 新增题目管理
     * 
     * @param fengProblem 题目管理
     * @return 结果
     */
    @Override
    public int insertFengProblem(FengProblem fengProblem)
    {
        fengProblem.setCreateTime(DateUtils.getNowDate());
        return fengProblemMapper.insertFengProblem(fengProblem);
    }

    /**
     * 修改题目管理
     * 
     * @param fengProblem 题目管理
     * @return 结果
     */
    @Override
    public int updateFengProblem(FengProblem fengProblem)
    {
        fengProblem.setUpdateTime(DateUtils.getNowDate());
        return fengProblemMapper.updateFengProblem(fengProblem);
    }

    /**
     * 批量删除题目管理
     * 
     * @param ids 需要删除的题目管理主键
     * @return 结果
     */
    @Override
    public int deleteFengProblemByIds(Long[] ids)
    {
        return fengProblemMapper.deleteFengProblemByIds(ids);
    }

    /**
     * 删除题目管理信息
     * 
     * @param id 题目管理主键
     * @return 结果
     */
    @Override
    public int deleteFengProblemById(Long id)
    {
        return fengProblemMapper.deleteFengProblemById(id);
    }
}
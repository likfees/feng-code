package cn.lzscxb.web.controller.business;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.lzscxb.domain.entity.FengTaskJoin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.lzscxb.domain.annotation.Log;
import cn.lzscxb.common.core.controller.BaseController;
import cn.lzscxb.domain.AjaxResult;
import cn.lzscxb.domain.enums.BusinessType;
import cn.lzscxb.domain.entity.FengProblemQueue;
import cn.lzscxb.business.service.IFengProblemQueueService;
import cn.lzscxb.common.utils.poi.ExcelUtil;
import cn.lzscxb.domain.page.TableDataInfo;

/**
 * 任务管理Controller
 * 
 * @author Likfees
 * @date 2022-12-08
 */
@RestController
@RequestMapping("/business/queue")
public class FengProblemQueueController extends BaseController
{
    @Autowired
    private IFengProblemQueueService fengProblemQueueService;
    /**
     * 查询任务管理列表
     */
    @PreAuthorize("@ss.hasPermi('business:queue:list')")
    @GetMapping("/list")
    public TableDataInfo list(FengProblemQueue fengProblemQueue)
    {
        startPage();
        List<FengProblemQueue> list = fengProblemQueueService.selectFengProblemQueueList(fengProblemQueue);
        return getDataTable(list);
    }

    /**
     * 排行榜列表
     */
    @PreAuthorize("@ss.hasPermi('business:rank:list')")
    @GetMapping("/rank-list")
    public TableDataInfo rankList(FengProblemQueue fengProblemQueue)
    {
        startPage();
        List<FengProblemQueue> list = fengProblemQueueService.selectFengProblemRankList(fengProblemQueue);
        return getDataTable(list);
    }

    /**
     * 查询任务管理列表
     */
    @PreAuthorize("@ss.hasPermi('business:queue:submit-list')")
    @GetMapping("/submit-list")
    public AjaxResult submitList(FengProblemQueue fengProblemQueue)
    {
        List<FengProblemQueue> list = fengProblemQueueService.submitList(fengProblemQueue);
        return AjaxResult.success(list);
    }



    /**
     * 查询待批阅学习任务
     */
    @PreAuthorize("@ss.hasPermi('business:queue:check-list')")
    @GetMapping("/check-list")
    public TableDataInfo checkList(FengProblemQueue fengProblemQueue)
    {
        startPage();
        List<FengProblemQueue> list = fengProblemQueueService.selectFengTaskCheckList(fengProblemQueue);
        return getDataTable(list);
    }


    /**
     * 执行任务
     */
    @PreAuthorize("@ss.hasPermi('business:queue:excute')")
    @GetMapping("/excute")
    public AjaxResult excuteQuque(FengProblemQueue fengProblemQueue)
    {
        FengProblemQueue result = fengProblemQueueService.excuteQuque(fengProblemQueue.getId());
        return AjaxResult.success(result);
    }

    /**
     * 导出任务管理列表
     */
    @PreAuthorize("@ss.hasPermi('business:queue:export')")
    @Log(title = "任务管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FengProblemQueue fengProblemQueue)
    {
        List<FengProblemQueue> list = fengProblemQueueService.selectFengProblemQueueList(fengProblemQueue);
        ExcelUtil<FengProblemQueue> util = new ExcelUtil<FengProblemQueue>(FengProblemQueue.class);
        util.exportExcel(response, list, "任务管理数据");
    }

    /**
     * 获取任务管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:queue:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(fengProblemQueueService.selectFengProblemQueueById(id));
    }

    /**
     * 新增任务管理
     */
    @PreAuthorize("@ss.hasPermi('business:queue:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FengProblemQueue fengProblemQueue)
    {
        return AjaxResult.success(fengProblemQueueService.insertFengProblemQueue(fengProblemQueue));
    }

    /**
     * 新增任务管理
     */
    @PreAuthorize("@ss.hasPermi('business:queue:check')")
    @PostMapping("check")
    public AjaxResult checkQueue(@RequestBody FengProblemQueue fengProblemQueue)
    {
        fengProblemQueue.setUpdateBy(getUsername());
        return AjaxResult.success(fengProblemQueueService.checkQueue(fengProblemQueue));
    }

    /**
     * 修改任务管理
     */
    @PreAuthorize("@ss.hasPermi('business:queue:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FengProblemQueue fengProblemQueue)
    {
        return toAjax(fengProblemQueueService.updateFengProblemQueue(fengProblemQueue));
    }

    /**
     * 删除任务管理
     */
    @PreAuthorize("@ss.hasPermi('business:queue:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fengProblemQueueService.deleteFengProblemQueueByIds(ids));
    }
}

package com.bjfu.fortree.service;

import com.bjfu.fortree.pojo.dto.ApplyJobDTO;
import com.bjfu.fortree.pojo.dto.TreeDTO;
import com.bjfu.fortree.pojo.dto.WoodlandDTO;
import com.bjfu.fortree.pojo.request.woodland.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 林地相关操作
 * @author warthog
 */
public interface WoodlandService {

    /**
     * 创建林地
     * @param userAccount 用户账号
     * @param request 创建林地的请求
     * @return 申请实体
     */
    ApplyJobDTO createWoodland(String userAccount, CreateWoodlandRequest request);

    /**
     * 为林地添加新记录
     * @param userAccount 用户账号
     * @param request 创建记录的请求
     * @return 申请实体
     */
    ApplyJobDTO addRecord(String userAccount, AddRecordRequest request);

    /**
     * 为记录添加树木
     * @param userAccount 用户账号
     * @param request 添加树木的请求
     * @return 申请实体
     */
    ApplyJobDTO addTrees(String userAccount, AddTreesRequest request);

    /**
     * 通过excel为记录添加树木
     * @param userAccount 用户账号
     * @param request 添加树木的请求
     * @return 申请实体
     */
    ApplyJobDTO addTreesByExcel(String userAccount, AddTreesByExcelRequest request);

    /**
     * 删除林地
     * @param userAccount 用户账号
     * @param woodlandId 林地id
     * @return 申请实体
     */
    ApplyJobDTO deleteWoodland(String userAccount, Long woodlandId);

    /**
     * 删除记录
     * @param userAccount 用户账号
     * @param recordId 记录id
     * @return 申请实体
     */
    ApplyJobDTO deleteRecord(String userAccount, Long recordId);

    /**
     * 删除树木
     * @param userAccount 用户账号
     * @param request 请求
     * @return 申请实体
     */
    ApplyJobDTO deleteTrees(String userAccount, DeleteTreesRequest request);

    /**
     * 编辑修改林地信息
     * @param userAccount 用户账号
     * @param request 请求
     * @return 申请实体
     */
    ApplyJobDTO editWoodland(String userAccount, EditWoodlandRequest request);

    /**
     * 修改林地记录信息
     * @param userAccount 用户账号
     * @param request 请求
     * @return 申请实体
     */
    ApplyJobDTO editRecord(String userAccount, EditRecordRequest request);

    /**
     * 获取林地列表
     * @param request 请求
     * @return 分页后的林地实体列表
     */
    Page<WoodlandDTO> getWoodlands(GetWoodlandsRequest request);

    /**
     * 获取所有的林地
     * @return 所有的林地实体
     */
    List<WoodlandDTO> getAllWoodlands();

    /**
     * 根据创建人获取林地列表
     * @param userAccount 用户账号
     * @param request 请求
     * @return 分页后的林地实体列表
     */
    Page<WoodlandDTO> getWoodlandsByCreator(String userAccount, GetWoodlandsByCreatorRequest request);

    /**
     * 获取林地详细信息
     * @param woodlandId 林地id
     * @return 林地详细信息
     */
    WoodlandDTO getWoodlandDetail(Long woodlandId);

    /**
     * 获取记录中的树木
     * @param request 请求
     * @return 树木列表
     */
    Page<TreeDTO> getTrees(GetTreesRequest request);

}

package com.bjfu.fortree.service;

import com.bjfu.fortree.dto.job.ApplyJobDTO;
import com.bjfu.fortree.dto.woodland.WoodlandDTO;
import com.bjfu.fortree.dto.woodland.WoodlandDetailDTO;
import com.bjfu.fortree.request.woodland.*;
import com.bjfu.fortree.vo.PageVO;

/**
 * 林地相关操作
 * @author warthog
 */
public interface WoodlandService {

    /**
     * 创建林地
     * @param userAccount 用户账号
     * @param createWoodlandRequest 创建林地的请求
     * @return 申请实体
     */
    ApplyJobDTO createWoodland(String userAccount, CreateWoodlandRequest createWoodlandRequest);

    /**
     * 为林地添加新记录
     * @param userAccount 用户账号
     * @param addRecordRequest 创建记录的请求
     * @return 申请实体
     */
    ApplyJobDTO addRecord(String userAccount, AddRecordRequest addRecordRequest);

    /**
     * 为记录添加树木
     * @param userAccount 用户账号
     * @param addTreesRequest 添加树木的请求
     * @return 申请实体
     */
    ApplyJobDTO addTrees(String userAccount, AddTreesRequest addTreesRequest);

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
     * @param deleteTreesRequest 请求
     * @return 申请实体
     */
    ApplyJobDTO deleteTrees(String userAccount, DeleteTreesRequest deleteTreesRequest);

    /**
     * 编辑修改林地信息
     * @param userAccount 用户账号
     * @param editWoodlandRequest 请求
     * @return 申请实体
     */
    ApplyJobDTO editWoodland(String userAccount, EditWoodlandRequest editWoodlandRequest);

    /**
     * 修改林地记录信息
     * @param userAccount 用户账号
     * @param editRecordRequest 请求
     * @return 申请实体
     */
    ApplyJobDTO editRecord(String userAccount, EditRecordRequest editRecordRequest);

    /**
     * 根据创建人获取林地
     * @param userAccount 用户账号
     * @param getWoodlandByCreatorRequest 请求
     * @return 分页后的林地实体
     */
    PageVO<WoodlandDTO> getWoodlandByCreator(String userAccount, GetWoodlandByCreatorRequest getWoodlandByCreatorRequest);

    /**
     * 获取林地详细信息
     * @param woodlandId 林地id
     * @return 林地详细信息
     */
    WoodlandDetailDTO getWoodlandDetail(Long woodlandId);

}

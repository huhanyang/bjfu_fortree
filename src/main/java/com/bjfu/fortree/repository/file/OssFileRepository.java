package com.bjfu.fortree.repository.file;

import com.bjfu.fortree.pojo.entity.OssFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * oss文件实体的持久接口
 * @author warthog
 */
public interface OssFileRepository extends JpaRepository<OssFile, Long> {
}

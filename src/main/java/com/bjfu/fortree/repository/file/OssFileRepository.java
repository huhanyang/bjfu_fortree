package com.bjfu.fortree.repository.file;

import com.bjfu.fortree.pojo.entity.file.OssFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author warthog
 */
public interface OssFileRepository extends JpaRepository<OssFile, Long> {
}

package com.architecture.archi.db.entity.file;

import com.architecture.archi.common.enumobj.BooleanFlag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "FILE")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "ORIGIN_NAME", length = 100, nullable = false)
    private String originName;

    @Column(name = "EXT", length = 20, nullable = false)
    private String ext;

    @Column(name = "SIZE", nullable = false)
    private Long size;

    @Column(name = "URL", length = 200, nullable = false)
    private String url;

    @Column(name = "PATH", length = 200, nullable = false)
    private String path;

    @Builder
    public FileEntity(Long id, String name, String originName, String ext, Long size, String url, String path) {
        this.id = id;
        this.name = name;
        this.originName = originName;
        this.ext = ext;
        this.size = size;
        this.url = url;
        this.path = path;
    }
}
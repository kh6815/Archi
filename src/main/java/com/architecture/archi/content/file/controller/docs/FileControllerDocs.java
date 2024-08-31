package com.architecture.archi.content.file.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.content.file.model.FileModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 API", description = "파일 관련 컨트롤러입니다.")
public interface FileControllerDocs {

    @Operation(summary = "파일 추가", description = "파일 추가 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 추가 완료", content = @Content(schema = @Schema(implementation = FileModel.FileRes.class)))
    })
    public ApiResponseModel<FileModel.FileRes> addFile(@RequestPart("file") MultipartFile file) throws CustomException;
}

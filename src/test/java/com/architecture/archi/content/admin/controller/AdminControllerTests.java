package com.architecture.archi.content.admin.controller;

import com.architecture.archi.content.admin.controller.AdminController;
import com.architecture.archi.content.admin.model.AdminTestModel;
import com.architecture.archi.content.admin.service.AdminReadService;
import com.architecture.archi.content.admin.service.AdminWriteService;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@MockBean(JpaMetamodelMappingContext.class)
@RequiredArgsConstructor
public class AdminControllerTests {

    private final MockMvc mockMvc;

    private final AdminReadService adminReadService;
    private final AdminWriteService adminWriteService;

    private final Gson gson;

//    @Test
//    @DisplayName("카테고리 등록 컨트롤러 로직 확인")
//    public void addCategoryTest() throws Exception {
//        // Given
//        AdminTestModel.AddCategoryReq addCategoryReq = new AdminTestModel.AddCategoryReq(0L, "카테고리");
//
//
//        // When
//
//        // Then
//    }

    @Test
    @WithMockUser(roles = "ADMIN") // ADMIN 권한을 가진 사용자로 테스트 수행
    @DisplayName("카테고리 등록 컨트롤러 로직 확인")
    public void addCategoryTest() throws Exception {
        // Given
        AdminTestModel.AddCategoryReq addCategoryReq = new AdminTestModel.AddCategoryReq(0L, "카테고리");
        String jsonContent = gson.toJson(addCategoryReq);


        // Mocking the adminWriteService.createCategory method
        Mockito.when(adminWriteService.createCategory(any(AdminTestModel.AddCategoryReq.class), any()))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(true));  // 응답 데이터에서 'data' 필드가 true인지 확인
    }
}
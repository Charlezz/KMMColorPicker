package com.bluestars.colorpicker.usecase

import com.bluestars.colorpicker.model.BitmapImage

object GetDominantColorUseCase {

    /**
     * @param image 비트맵 이미지
     * @return 대표 색상
     */
    suspend operator fun invoke(
        image:BitmapImage
    ):Int{
        // 대표 색상 계산 로직 작성
        return 0
    }
}
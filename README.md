# 2026IronMan

教材勘誤與版本更新請見 [ERRATA.md](ERRATA.md)。

## Day 19：validation responsibility

課前起點是 [day19/start](https://github.com/YuanLiou/2026IronMan/tree/day19/start)。完成把 validation responsibility 交給 `DiaryValidator` 的教材狀態是 [day19/validation-extracted](https://github.com/YuanLiou/2026IronMan/tree/day19/validation-extracted)。

若要跟隨既有 Day 19 內容，使用 `git switch day19/validation-extracted`。若要額外學習如何直接以 JVM Unit Test 驗證不依賴 Android runtime 的 `DiaryValidator`，使用補充版本 `git switch day19/validation-extracted-v2`；這個版本保留原本的 App 行為與 Android instrumented tests，只新增 direct Unit Test。請先自行嘗試，不要立即開啟。

補充版本入口：[day19/validation-extracted-v2](https://github.com/YuanLiou/2026IronMan/tree/day19/validation-extracted-v2)。

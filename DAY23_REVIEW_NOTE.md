# Day 23 code review 反例

這個 branch 是 Day 23 用來比較的公開反例。它刻意保留 Day 22 的可觀察行為，並把目前的搜尋、心情篩選與排序投影拆到額外的 provider、use case、query composition 與 mapper 層，方便課堂檢視抽象化帶來的成本。

它不是 official chain，不能當作 Day 24 起點。請把它和 official `day22/mood-filter` 比較，並以比較結果討論哪些責任分配與 indirection 有足夠證據支持。

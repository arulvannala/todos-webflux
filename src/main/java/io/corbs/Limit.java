package io.corbs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Limit {
    private Integer limit;
    private Integer size;
    private Integer nextId;
}

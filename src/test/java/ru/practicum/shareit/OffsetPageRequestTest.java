package ru.practicum.shareit;


import org.junit.jupiter.api.Test; import org.springframework.data.domain.Sort; import static org.assertj.core.api.Assertions.assertThat; import static org.assertj.core.api.Assertions.assertThatThrownBy;
class OffsetPageRequestTest {
    @Test void testOfMethodWithValidOffsetAndSizeThenReturnOffsetPageRequest() {
        Long offset = 10L; Integer size = 5; OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);
        assertThat(pageRequest.getOffset()).isEqualTo(offset);
        assertThat(pageRequest.getPageSize()).isEqualTo(size);
        assertThat(pageRequest.getSort()).isEqualTo(Sort.unsorted());
    }

    @Test
    void testGetPageNumberMethodWithValidOffsetAndSizeThenReturnPageNumber() {
        Long offset = 20L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);

        int expectedPageNumber = (int) (offset / size);
        assertThat(pageRequest.getPageNumber()).isEqualTo(expectedPageNumber);
    }

    @Test
    void testGetPageSizeMethodWithValidOffsetAndSizeThenReturnPageSize() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);

        assertThat(pageRequest.getPageSize()).isEqualTo(size);
    }

    @Test
    void testGetOffsetMethodWithValidOffsetAndSizeThenReturnOffset() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);

        assertThat(pageRequest.getOffset()).isEqualTo(offset);
    }

    @Test
    void testGetSortMethodThenReturnUnsortedSort() {
        OffsetPageRequest pageRequest = OffsetPageRequest.of(10L, 5);

        assertThat(pageRequest.getSort()).isEqualTo(Sort.unsorted());
    }

    @Test
    void testNextMethodThenReturnNewOffsetPageRequest() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);
        OffsetPageRequest nextPageRequest = (OffsetPageRequest) pageRequest.next();

        assertThat(nextPageRequest.getOffset()).isEqualTo(offset + size);
        assertThat(nextPageRequest.getPageSize()).isEqualTo(size);
    }

    @Test
    void testPreviousOrFirstMethodThenReturnNewOffsetPageRequest() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);
        OffsetPageRequest previousPageRequest = (OffsetPageRequest) pageRequest.previousOrFirst();

        assertThat(previousPageRequest.getOffset()).isEqualTo(Math.max(0, offset - size));
        assertThat(previousPageRequest.getPageSize()).isEqualTo(size);
    }

    @Test
    void testFirstMethodThenReturnNewOffsetPageRequest() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);
        OffsetPageRequest firstPageRequest = (OffsetPageRequest) pageRequest.first();

        assertThat(firstPageRequest.getOffset()).isEqualTo(0);
        assertThat(firstPageRequest.getPageSize()).isEqualTo(size);
    }

    @Test
    void testHasPreviousMethodThenReturnCorrectBooleanValue() {
        Long offset = 10L;
        Integer size = 5;
        OffsetPageRequest pageRequest = OffsetPageRequest.of(offset, size);

        assertThat(pageRequest.hasPrevious()).isTrue();
    }

    @Test
    void testOfMethodWithNegativeOffsetThenThrowIllegalArgumentException() {
        Long offset = -1L;
        Integer size = 5;

        assertThatThrownBy(() -> OffsetPageRequest.of(offset, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Offset must be positive or zero!");
    }

    @Test
    void testOfMethodWithNullOffsetThenThrowIllegalArgumentException() {
        Integer size = 5;

        assertThatThrownBy(() -> OffsetPageRequest.of(null, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Offset must be positive or zero!");
    }

    @Test
    void testOfMethodWithNegativeSizeThenThrowIllegalArgumentException() {
        Long offset = 10L;
        Integer size = -1;

        assertThatThrownBy(() -> OffsetPageRequest.of(offset, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be positive!");
    }

    @Test
    void testOfMethodWithZeroSizeThenThrowIllegalArgumentException() {
        Long offset = 10L;

        assertThatThrownBy(() -> OffsetPageRequest.of(offset, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be positive!");
    }

    @Test
    void testOfMethodWithNullSizeThenThrowIllegalArgumentException() {
        Long offset = 10L;

        assertThatThrownBy(() -> OffsetPageRequest.of(offset, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be positive!");
    }
}

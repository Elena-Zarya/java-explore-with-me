package ru.practicum.mainservice;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Pages {
    public static PageRequest getPage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public static PageRequest getPage(int from, int size, org.springframework.data.domain.Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}

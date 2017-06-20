package application;

public enum UserAction {
    EXIT,

    PRINT_LIST,
    EDIT_LIST,
    LOAD_LIST,
    SAVE_LIST,

    NO_FILTER,
    FILTER_BOOKS,
    FILTER_DUAL_CORES,

    NO_SORT,
    SORT_BY_PRICE,
    SORT_BY_ID_REVERSED,

    ADD,
    EDIT,
    DELETE,

    WRONG_INPUT,
}

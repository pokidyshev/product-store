package application;

final class Resources {

    final static String mainMenu =
            "\n********** Главное Меню ***********\n" +
                    "0\t Выход\n" +
                    "1\t Распечать...\n" +
                    "2\t Изменить...\n" +
                    "3\t Загрузить...\n" +
                    "4\t Сохранить...\n" +
            "***********************************\n";

    final static String filterOptions =
            "\nВыберите фильтры:\n" +
                    "0\t Без фильтров\n" +
                    "1\t Только книги\n" +
                    "2\t Только двухядерные ноутбуки\n";


    final static String sortOptions =
            "\nВыберите сортировку:\n" +
                    "0\t Без сортировки\n" +
                    "1\t По цене\n" +
                    "2\t По идентификатору\n";

    final static String changeProductList =
            "\nВыберите, что сделать со списком:\n" +
                    "0\t Добавить продукт\n" +
                    "1\t Изменить продукт\n" +
                    "2\t Удалить продукт\n";

    final static String loadOptions = "\nГрузим из БД? (Нет - из файла)";


    final static String saveOptions = "\nСохраняем в БД? (Нет - в файл)";


    final static String deletePrompt = "\nВы хотите удалить один товар?";


    final static String productTypePrompt =
            "\nВведите, товар какого типа вы хотите создать:\n" +
            "0. Книга\n" +
            "1. Ноутбук\n";

    final static String wrongMenuItemInput =
            "Неправильно выбран пункт меню!";
}

package application;

import models.PersistentStore;
import models.ProductUtils;
import models.products.Book;
import models.products.Laptop;
import models.products.Product;
import models.products.ProductType;

import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class Application {

    public static void run() {
        UserAction menu_item;
        UserAction[] mainActions = new UserAction[] {
                UserAction.EXIT, UserAction.PRINT_LIST, UserAction.EDIT_LIST,
                UserAction.LOAD_LIST, UserAction.SAVE_LIST
        };

        do {
            menu_item = UserInteraction.askForUserAction(Resources.mainMenu, mainActions);
            actions.get(menu_item).run();
        } while (menu_item != UserAction.EXIT);
    }

    // region Private constants & fields

    private static final HashMap<UserAction, Runnable> actions = new HashMap<UserAction, Runnable>() {
        {
            // ERROR
            put(UserAction.WRONG_INPUT, Application::warnAboutWrongInput);

            // MAIN MENU
            put(UserAction.EXIT, Application::sayFarewell);
            put(UserAction.PRINT_LIST, Application::printWithOptions);
            put(UserAction.EDIT_LIST, Application::editList);
            put(UserAction.LOAD_LIST, Application::loadList);
            put(UserAction.SAVE_LIST, Application::saveList);

            // EDIT LIST
            put(UserAction.ADD, Application::add);
            put(UserAction.EDIT, Application::edit);
            put(UserAction.DELETE, Application::delete);
        }
    };

    private interface Converter extends UnaryOperator<List<Product>> {}

    private static final HashMap<UserAction, Converter> converters =
            new HashMap<UserAction, Converter>() {
        {
            put(UserAction.NO_FILTER, (l) -> l);
            put(UserAction.FILTER_BOOKS, ProductUtils::filterBooks);
            put(UserAction.FILTER_DUAL_CORES, ProductUtils::filterDualCoreLaptops);

            put(UserAction.NO_SORT, (l) -> l);
            put(UserAction.SORT_BY_PRICE, ProductUtils::sortedByPrice);
            put(UserAction.SORT_BY_ID_REVERSED, ProductUtils::sortedByIdReversed);
        }
    };

    private static PersistentStore store = new PersistentStore();

    // endregion

    private Application() {}

    // region Private methods

    private static void print() {
        if (store.count() == 0) {
            UserInteraction.say("В магазине нет ни одного товара.");
        } else {
            UserInteraction.say(
                    store.toList()
                            .stream()
                            .map(Product::toString)
                            .collect(Collectors.toList())
            );
        }
    }

    private static void warnAboutWrongInput() {
        UserInteraction.say(Resources.wrongMenuItemInput);
    }

    private static ProductType fromInt(int value){
        switch (value){
            case 0:
                return ProductType.BOOK;
            case 1:
                return ProductType.LAPTOP;
            default:
                return ProductType.NONE;
        }
    }

    // region Main menu

    private static void sayFarewell() {
        UserInteraction.say("Приходите к нам еще!");
    }

    private static void printWithOptions() {
        if (store.count() == 0) {
            UserInteraction.say("В магазине нет ни одного товара.");
            return;
        }

        UserAction menu_item = UserInteraction.askForUserAction(
                Resources.filterOptions,
                new UserAction[] { UserAction.NO_FILTER, UserAction.FILTER_BOOKS, UserAction.FILTER_DUAL_CORES}
        );
        if (menu_item == UserAction.WRONG_INPUT)
            return;
        Converter filter = converters.get(menu_item);

        menu_item = UserInteraction.askForUserAction(
                Resources.sortOptions,
                new UserAction[] { UserAction.NO_SORT, UserAction.SORT_BY_PRICE, UserAction.SORT_BY_ID_REVERSED}
        );
        if (menu_item == UserAction.WRONG_INPUT)
            return;
        Converter sort = converters.get(menu_item);

        UserInteraction.say(
                filter.andThen(sort).apply(store.toList())
                .stream()
                .map(Product::toString)
                .collect(Collectors.toList())
        );
    }

    private static void editList() {
        UserAction[] editListActions = new UserAction[] { UserAction.ADD, UserAction.EDIT, UserAction.DELETE };
        UserAction menu_item = UserInteraction.askForUserAction(Resources.changeProductList, editListActions);
        actions.get(menu_item).run();
    }

    private static void saveList() {
        boolean allCool;

        if (UserInteraction.askYesOrNo(Resources.saveOptions)) {
            allCool = store.saveToDB();
        } else {
            allCool = store.saveToFile();
        }

        if (allCool){
            UserInteraction.say("Сохранение прошло успешно");
        }
        else {
            UserInteraction.say("Сохранение завершилось неудачно");
        }
    }

    private static void loadList() {
        boolean allCool;

        if (UserInteraction.askYesOrNo(Resources.loadOptions)) {
            allCool = store.loadFromDB();
        } else {
            allCool = store.loadFromFile();
        }

        if (allCool) {
            UserInteraction.say("Загрузка прошла успешно");
            if (UserInteraction.askYesOrNo("Хотите отобразить результат?")){
                print();
            }
        } else {
            UserInteraction.say("Загрузка завершилась неудачно");
        }
    }

    // endregion

    // region Edit List

    private static void add() {

        ProductType kind = fromInt(UserInteraction.getInt(Resources.productTypePrompt));
        if (kind == ProductType.NONE){
            UserInteraction.say("Неизвестный тип товара");
            return;
        }

        long id = UserInteraction.getInt("Введите ID");
        if (store.hasProductWithId(id)) {
            UserInteraction.say("Товар с таким ID уже есть!");
            return;
        }

        Double price = UserInteraction.getDouble("Введите цену товара");
        if (price < 0) {
            UserInteraction.say("Цена товара не может быть отрицательной!");
            return;
        }

        String intPrompt, stringPrompt;
        if (kind == ProductType.BOOK) {
            intPrompt = "Введите количество страниц";
            stringPrompt = "Введите название";
        } else {
            intPrompt = "Введите количество ядер";
            stringPrompt = "Введите модель";
        }

        int intVal = UserInteraction.getInt(intPrompt);
        String strVal = UserInteraction.ask(stringPrompt);

        Product p;
        if (kind == ProductType.BOOK) {
            p = new Book(id, price, strVal, intVal);
        } else {
            p = new Laptop(id, price, strVal, intVal);
        }

        store.put(p);
        UserInteraction.say("Товар добавлен.");
    }

    private static void delete() {
        print();

        boolean one = UserInteraction.askYesOrNo(Resources.deletePrompt);
        boolean removed = true;

        if (one) {
            long id = UserInteraction.getInt("Введите ID");
            removed = store.remove(id) != null;
            UserInteraction.say(removed ? "Удален" : "Не найден");
        } else {
            long id1 = UserInteraction.getInt("Введите первую границу (ID)");
            long id2 = UserInteraction.getInt("Введите вторую границу (ID)");
            store.remove(Math.min(id1, id2), Math.max(id1, id2));
            UserInteraction.say("Товары, удовлетворяющие заданному критерию, удалены");
        }

        if (removed && UserInteraction.askYesOrNo("Распечатать результирующий список?"))
            print();
    }

    private static void edit() {
        print();

        long id = UserInteraction.getInt("Введите ID");
        if (!store.hasProductWithId(id)) {
            UserInteraction.say("Товар с таким ID не существует!");
            return;
        }
        Product p = store.get(id);

        Double price = UserInteraction.getDouble("Введите новую цену товара");
        if (price < 0) {
            UserInteraction.say("Цена товара не может быть отрицательной!");
            return;
        }
        p.setPrice(price);

        String intPrompt, stringPrompt;
        if (p.getType() == ProductType.BOOK) {
            intPrompt = "Введите количество страниц";
            stringPrompt = "Введите название";
        } else {
            intPrompt = "Введите количество ядер";
            stringPrompt = "Введите модель";
        }

        int intVal = UserInteraction.getInt(intPrompt);
        String strVal = UserInteraction.ask(stringPrompt);

        if (p.getType() == ProductType.BOOK) {
            ((Book)p).setTitle(strVal);
            ((Book)p).setPages(intVal);
        } else {
            ((Laptop)p).setModel(strVal);
            ((Laptop)p).setCores(intVal);
        }

        store.put(p);
        UserInteraction.say("Товар изменен успешно.");

        if (UserInteraction.askYesOrNo("Хотите ли посмотреть измененный список")) {
            print();
        }
    }

    // endregion

    // endregion
}

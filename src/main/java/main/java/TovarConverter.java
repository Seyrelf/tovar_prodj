package main.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.model.Tovar;
import main.java.model.Tovar_for_tableview;
import main.java.service.TovarService;
import java.util.ArrayList;
import java.util.List;

public class TovarConverter {

    private ObservableList<Tovar_for_tableview> list_for_tovar;
    private TovarService tovarService = new TovarService();

    public ObservableList<Tovar_for_tableview> all_tovar_for_create_dialog(){
        list_for_tovar = FXCollections.observableArrayList();
        for(Tovar t : tovarService.findAllTovar()){
            list_for_tovar.add(convert_tovar_for_table(t));
        }
        return list_for_tovar;
    }

    public ObservableList<Tovar_for_tableview> tovar_for_move_dialog(Tovar tovar){
        List<Integer> empty_list = new ArrayList<>();
        List<Integer> child_id_and_self_and_parent = tovarService.getAllChildAndSelfId(tovar, empty_list);
        child_id_and_self_and_parent.add(tovar.getId_p());
        list_for_tovar = FXCollections.observableArrayList();
        for(Tovar t : tovarService.findAllTovar()){
            if(child_id_and_self_and_parent.contains(t.getId())){
                continue;
            }
            else {
                list_for_tovar.add(convert_tovar_for_table(t));
            }
        }
        return list_for_tovar;
    }

    public Tovar_for_tableview convert_tovar_for_table(Tovar tovar){
        return new Tovar_for_tableview(tovar.getId(),String.valueOf(tovar.getObject_info().get("Название")),
                String.valueOf(tovar.getObject_info().get("Тип продукта")));
    }

    public Tovar convert_tovar_from_table(Tovar_for_tableview tovar){
        return tovarService.findById(tovar.getId());
    }
}

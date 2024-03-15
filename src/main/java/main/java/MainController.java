package main.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import main.java.model.*;
import main.java.model.Component;
import main.java.service.TovarService;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MainController implements Initializable {
    private ObservableList<Tovar_for_tableview> list_for_tovar;
    private ObservableList<String> list_for_type;
    private Map<String ,Object> tovar_info;
    private TovarConverter tovarConverter;
    private TovarService tovarService;
    private Dialog<ButtonType> dialog = new Dialog<>();
    private Optional<ButtonType> clickedButton;
    private ObjectMapper mapper = new ObjectMapper();
    @FXML
    public TableView<Tovar_for_tableview> tovar_tableview;
    @FXML
    public TableColumn<Tovar_for_tableview,Integer> tovar_id;
    @FXML
    public TableColumn<Tovar_for_tableview,String> tovar_name;
    @FXML
    public TableColumn<Tovar_for_tableview,String> tovar_type;
    @FXML
    public TextField textfield_name;
    @FXML
    public TextField textfield_tare;
    @FXML
    public TextField textfield_netto;
    @FXML
    public TextField textfield_party;
    @FXML
    public ChoiceBox<Tovar_for_tableview> place_choicebox;
    @FXML
    public ChoiceBox<String > type_choicebox;
    @FXML
    public Label name_tovar_label;
    @FXML
    public Label type_tovar_label;
    @FXML
    public Label netto_tovar_label;
    @FXML
    public Label tare_tovar_label;
    @FXML
    public Label bruto_tovar_label;
    @FXML
    public Label netto_child_label;
    @FXML
    public Label tare_child_label;
    @FXML
    public Label bruto_child_label;
    @FXML
    public Label id_tovar_label;
    @FXML
    public Label idp_tovar_label;
    @FXML
    public AnchorPane scene_for_object;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tovarConverter = new TovarConverter();
        tovarService = new TovarService();
        update_table();
    }

    public void save_pos_on_scene_btn(ActionEvent event){
        for(Node node:scene_for_object.getChildren()){
            Group g = (Group) node;
            if(g.getChildren().size() == 4){
                Label id = (Label) ((Group) node).getChildren().get(1);
                Integer id_tovar = Integer.valueOf(id.getText().substring(3));
                tovarService.update_tovar_info_cordinate(id_tovar,node.getLayoutX(), node.getLayoutY());}}}

    public void show_visual_btn(ActionEvent event) throws IOException {
        scene_for_object.getChildren().clear();
        try {
            Tovar_for_tableview tovar = tovar_tableview.getSelectionModel().getSelectedItem();
            vis(tovarConverter.convert_tovar_from_table(tovar),new Group());}
        catch (Exception e){
            get_dialog_error();}}

    public void clear_scene_btn(ActionEvent event){
        scene_for_object.getChildren().clear();
    }

    public void show_dialog_change_btn(ActionEvent event) throws IOException {
        Tovar tovar;
        try{
            Tovar_for_tableview tovar_for_tableview = tovar_tableview.getSelectionModel().getSelectedItem();
            tovar = tovarConverter.convert_tovar_from_table(tovar_for_tableview);
        }
        catch (Exception e){
            get_dialog_error();
            return;
        }
        dialog = create_dialog("/visu/change_tovar_dialog.fxml");
        tovar_info = tovar.getObject_info();
        textfield_name.setText((String) tovar_info.get("Название"));
        textfield_party.setText(String.valueOf(tovar_info.get("Партия")));
        textfield_netto.setText(String.valueOf(tovar_info.get("Вес нетто")));
        textfield_tare.setText(String.valueOf(tovar_info.get("Вес тары")));
        clickedButton = dialog.showAndWait();
        if(clickedButton.get() == ButtonType.FINISH){
            if(check_digital()){
                return;
            }
            tovar_info.put("Название",textfield_name.getText());
            tovar_info.put("Партия",textfield_party.getText());
            tovar_info.put("Вес тары",Double.valueOf(textfield_tare.getText()));
            tovar_info.put("Вес нетто",Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес бруто",Double.valueOf(textfield_tare.getText()) + Double.valueOf(textfield_netto.getText()));
            tovarService.update_tovar_info(tovar,tovar_info);
            update_table();
            scene_for_object.getChildren().clear();
        }

    }

    public void show_dialog_move_btn(ActionEvent event) throws IOException {
        dialog = create_dialog("/visu/move_tovar_dialog.fxml");
        Tovar tovar = new Tovar();
        try {
            tovar = tovarConverter.convert_tovar_from_table(tovar_tableview.getSelectionModel().getSelectedItem());
        }
        catch (Exception e){
            get_dialog_error();
            return;
        }
        place_choicebox.setItems(tovarConverter.tovar_for_move_dialog(tovar));
        clickedButton = dialog.showAndWait();
        if(clickedButton.get() == ButtonType.FINISH ){
            try {
                tovarService.move_tovar(tovar,tovarConverter.convert_tovar_from_table(place_choicebox.getValue()));
                update_table();
                scene_for_object.getChildren().clear();
            }
            catch (Exception e){
                get_dialog_error();
            }
        }
    }

    public void delete_tovar_btn(ActionEvent event) throws IOException {
        scene_for_object.getChildren().clear();
        Tovar tovar = null;
        try{
            tovar = tovarConverter.convert_tovar_from_table(tovar_tableview.getSelectionModel().getSelectedItem());
        }
        catch (Exception e){
            get_dialog_error();
            return;
        }
        tovarService.deleteTovar(tovar);
        update_table();
    }

    public void show_choose_tovar_type_dialog_btn(ActionEvent event) throws IOException {
        dialog = create_dialog("/visu/tovar_type_dialog.fxml");
        list_for_type = FXCollections.observableArrayList();
        list_for_type.addAll("Товарное место","Тара","Компонент","Продукт");
        type_choicebox.setItems(list_for_type);
        clickedButton = dialog.showAndWait();
        if(clickedButton.get() == ButtonType.OK){
            switch (type_choicebox.getValue()){
                case ("Товарное место"):
                    show_dialog_create_place();
                    break;
                case ("Тара"):
                    show_dialog_create_tare();
                    break;
                case ("Компонент"):
                    show_dialog_create_component();
                    break;
                case ("Продукт"):
                    show_dialog_create_product();
                    break;
                default:
                    break;
            }
        }
    }

    public void close_program_btn(ActionEvent event){
        System.exit(0);
    }

    public Dialog<ButtonType> create_dialog(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(path));
        fxmlLoader.setController(this);
        DialogPane type_dialog = fxmlLoader.load();
        dialog.setDialogPane(type_dialog);
        return dialog;
    }

    public void get_dialog_error() throws  IOException{
        dialog = create_dialog("/visu/error_dialog.fxml");
        dialog.show();
    }

    public void get_dialog_error_param() throws  IOException{
        dialog = create_dialog("/visu/error_dialog_param.fxml");
        dialog.show();
    }

    public void show_dialog_create_place() throws IOException {
        dialog = create_dialog("/visu/create_place_dialog.fxml");
        place_choicebox.setItems(tovarConverter.all_tovar_for_create_dialog());
        clickedButton = dialog.showAndWait();
        if (clickedButton.get() == ButtonType.FINISH){
            tovar_info = new HashMap<>();
            tovar_info.put("Название",textfield_name.getText());
            tovar_info.put("Партия",textfield_party.getText());
            tovar_info.put("Тип продукта","Товарное место");
            tovar_info.put("Узлы", new ArrayList<>());
            tovar_info.put("Вес тары",0.0);
            tovar_info.put("Вес нетто",0.0);
            tovar_info.put("Вес бруто",0.0);
            tovar_info.put("Вес тары узлов",0.0);
            tovar_info.put("Вес нетто узлов",0.0);
            tovar_info.put("Вес бруто узлов",0.0);
            tovar_info.put("x",0.0);
            tovar_info.put("y",0.0);
            int id_p;
            try {
                id_p = place_choicebox.getValue().getId();
            }
            catch (Exception e){
                id_p = 0;
            }
            Place place = new Place(tovar_info,id_p);
            Tovar tovar = new Tovar(place.getObject_info(),place.getId_p());
            tovarService.saveTovar(tovar);
            update_table();
        }}

    public void show_dialog_create_tare() throws IOException {
        dialog = create_dialog("/visu/create_tare_dialog.fxml");
        place_choicebox.setItems(tovarConverter.all_tovar_for_create_dialog());
        clickedButton = dialog.showAndWait();
        if (clickedButton.get() == ButtonType.FINISH){
            if(check_digital()){
                return;
            }
            tovar_info = new HashMap<>();
            tovar_info.put("Название",textfield_name.getText());
            tovar_info.put("Партия",textfield_party.getText());
            tovar_info.put("Тип продукта","Тара");
            tovar_info.put("Узлы", new ArrayList<>());
            tovar_info.put("Вес тары",Double.valueOf(textfield_tare.getText()));
            tovar_info.put("Вес нетто",Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес бруто",Double.valueOf(textfield_tare.getText())+Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес тары узлов",0.0);
            tovar_info.put("Вес нетто узлов",0.0);
            tovar_info.put("Вес бруто узлов",0.0);
            tovar_info.put("x",0.0);
            tovar_info.put("y",0.0);
            int id_p;
            try {
                id_p = place_choicebox.getValue().getId();
            }
            catch (Exception e){
                id_p = 0;
            }
            Tare tare = new Tare(tovar_info,id_p);
            Tovar tovar = new Tovar(tare .getObject_info(),tare .getId_p());
            tovarService.saveTovar(tovar);
            update_table();
        }}

    public void show_dialog_create_product() throws IOException {
        dialog = create_dialog("/visu/create_product_dialog.fxml");
        place_choicebox.setItems(tovarConverter.all_tovar_for_create_dialog());
        clickedButton = dialog.showAndWait();
        if (clickedButton.get() == ButtonType.FINISH){
            if(check_digital()){
                return;
            }
            tovar_info = new HashMap<>();
            tovar_info.put("Название",textfield_name.getText());
            tovar_info.put("Партия",textfield_party.getText());
            tovar_info.put("Тип продукта","Продукт");
            tovar_info.put("Узлы", new ArrayList<>());
            tovar_info.put("Вес тары",Double.valueOf(textfield_tare.getText()));
            tovar_info.put("Вес нетто",Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес бруто",Double.valueOf(textfield_tare.getText())+Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес тары узлов",0.0);
            tovar_info.put("Вес нетто узлов",0.0);
            tovar_info.put("Вес бруто узлов",0.0);
            tovar_info.put("x",0.0);
            tovar_info.put("y",0.0);
            int id_p;
            try {
                id_p = place_choicebox.getValue().getId();
            }
            catch (Exception e){
                id_p = 0;
            }
            Product product = new Product(tovar_info,id_p);
            Tovar tovar = new Tovar(product.getObject_info(),product.getId_p());
            tovarService.saveTovar(tovar);
            update_table();
        }}

    public void show_dialog_create_component() throws IOException {
        dialog = create_dialog("/visu/create_component_dialog.fxml");
        place_choicebox.setItems(tovarConverter.all_tovar_for_create_dialog());
        clickedButton = dialog.showAndWait();
        if (clickedButton.get() == ButtonType.FINISH){
            if(check_digital()){
                return;
            }
            tovar_info = new HashMap<>();
            tovar_info.put("Название",textfield_name.getText());
            tovar_info.put("Партия",textfield_party.getText());
            tovar_info.put("Тип продукта","Компонент");
            tovar_info.put("Узлы", new ArrayList<>());
            tovar_info.put("Вес тары",Double.valueOf(textfield_tare.getText()));
            tovar_info.put("Вес нетто",Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес бруто",Double.valueOf(textfield_tare.getText())+Double.valueOf(textfield_netto.getText()));
            tovar_info.put("Вес тары узлов",0.0);
            tovar_info.put("Вес нетто узлов",0.0);
            tovar_info.put("Вес бруто узлов",0.0);
            tovar_info.put("x",0.0);
            tovar_info.put("y",0.0);
            int id_p;
            try {
                id_p = place_choicebox.getValue().getId();
            }
            catch (Exception e){
                id_p = 0;
            }
            Component component = new Component(tovar_info,id_p);
            Tovar tovar = new Tovar(component.getObject_info(),component.getId_p());
            tovarService.saveTovar(tovar);
            update_table();
        }}

    public Boolean check_digital() throws IOException {
        try {
            Double tare = Double.valueOf(textfield_tare.getText());
            Double netto = Double.valueOf(textfield_netto.getText());
            Integer party = Integer.valueOf(textfield_party.getText());
        }
        catch (Exception e){
            get_dialog_error_param();
            return true;
        }
        return false;

    }

    public void vis(Tovar tovar,Group old_object){
        tovar_info = tovar.getObject_info();
        old_object = show_object(tovar, old_object);
        List<Tovar> tovar_child = (ArrayList<Tovar>) tovar_info.get("Узлы");
        for(int i = 0; i < tovar_child.toArray().length;i++){
            vis(mapper.convertValue(tovar_child.get(i),Tovar.class),old_object);}
    }

    public Group show_object(Tovar tovar,Group old_obj){
        tovar_info = tovar.getObject_info();
        ImageView img;
        switch (String.valueOf(tovar_info.get("Тип продукта"))){
            case ("Товарное место"):
                img = new ImageView("image/place.png");
                break;
            case ("Тара"):
                img = new ImageView("image/tare.png");
                break;
            case ("Компонент"):
                img = new ImageView("image/component.png");
                break;
            case ("Продукт"):
                img = new ImageView("image/product.png");
                break;
            default:
                img = new ImageView("image/place.png");
                break;}
        img.setFitHeight(70);
        img.setFitWidth(70);

        Label id_p = new Label(String.valueOf(tovar.getId_p()));
        id_p.setVisible(false);

        Label name = new Label((String) tovar_info.get("Название"));
        name.setLayoutX(5);
        name.setLayoutY(-15);
        name.setFont(new Font("Arial Black", 13));

        Label id = new Label("ID:" + tovar.getId());
        id.setLayoutY(75);
        id.setLayoutX(5);
        id.setFont(new Font("Arial Black", 13));
        Group object = new Group();
        object.getChildren().addAll(img,id,name,id_p);
        object.setLayoutX((Double) tovar.getObject_info().get("x"));
        object.setLayoutY((Double) tovar.getObject_info().get("y"));

        if(!scene_for_object.getChildren().isEmpty()){
            Group line_object = new Group();
            Label id_line = new Label(String.valueOf(tovar.getId()));
            Label idp_line = new Label(String.valueOf(tovar.getId_p()));
            Line line = new Line(old_obj.getLayoutX(),old_obj.getLayoutY(),object.getLayoutX(),object.getLayoutY());
            idp_line.setVisible(false);
            id_line.setVisible(false);
            line_object.getChildren().addAll(idp_line,id_line,line);
            scene_for_object.getChildren().add(line_object);
        }
        scene_for_object.getChildren().add(object);

        object.setOnMouseDragged(e -> {
            if((e.getScreenX() - 660 > 0) && (e.getScreenX()-640 < 920) && (e.getScreenY() - 200 > 0) && (e.getScreenY() - 160 < 700)){
                object.setLayoutX(e.getSceneX() -340);
                object.setLayoutY(e.getSceneY() - 80);
                Label object_label = (Label) object.getChildren().get(1);
                String object_id = object_label.getText().substring(3);
                for(Node node:scene_for_object.getChildren()){
                    Group g = (Group) node;
                    if(g.getChildren().size() == 3){
                        Label id_label = (Label) g.getChildren().get(1);
                        Label idp_label = (Label) g.getChildren().get(0);
                        Line line = (Line) g.getChildren().get(2);
                        if(id_label.getText().equals(object_id)){
                            line.setEndX(object.getLayoutX());
                            line.setEndY(object.getLayoutY());
                        }
                        else if(idp_label.getText().equals(object_id)){
                            line.setStartX(object.getLayoutX());
                            line.setStartY(object.getLayoutY());
                        }
                    }
                }
            }
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        object.setOnMouseClicked(e->{
            if(e.getClickCount() == 2){
                try {
                    tovar_info = tovar.getObject_info();
                    dialog = create_dialog("/visu/tovar_info_dialog.fxml");
                    name_tovar_label.setText((String) tovar_info.get("Название"));
                    type_tovar_label.setText(String.valueOf( tovar_info.get("Тип продукта")));
                    netto_tovar_label.setText(String.valueOf(tovar_info.get("Вес тары")));
                    tare_tovar_label.setText(String.valueOf(tovar_info.get("Вес нетто")));
                    bruto_tovar_label.setText(String.valueOf(tovar_info.get("Вес бруто")));
                    netto_child_label.setText(String.valueOf(tovar_info.get("Вес нетто узлов")));
                    tare_child_label.setText(String.valueOf(tovar_info.get("Вес тары узлов")));
                    bruto_child_label.setText(String.valueOf(tovar_info.get("Вес бруто узлов")));
                    id_tovar_label.setText(String.valueOf(tovar.getId()));
                    idp_tovar_label.setText(String.valueOf(tovar.getId_p()));
                    dialog.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});

        return  object;
    }

    public void init_table_view(){
        tovar_id.setCellValueFactory(new PropertyValueFactory<Tovar_for_tableview,Integer>("id"));
        tovar_name.setCellValueFactory(new PropertyValueFactory<Tovar_for_tableview,String>("name"));
        tovar_type.setCellValueFactory(new PropertyValueFactory<Tovar_for_tableview,String>("type"));
    }

    public void update_table(){
        init_table_view();
        list_for_tovar = FXCollections.observableArrayList();
        for(Tovar tovar : tovarService.findAllTovar()){
            list_for_tovar.add(tovarConverter.convert_tovar_for_table(tovar));
        }
        tovar_tableview.setItems(list_for_tovar);
    }
}

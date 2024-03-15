package main.java.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.model.Tovar;
import main.java.repository.TovarDao;
import java.util.*;

public class TovarService {

    private Double tare;
    private Double netto;
    private Tovar tovar_parent;
    private Tovar tovar_parent_with_old_info;
    ObjectMapper mapper = new ObjectMapper();
    private Map<String,Object> tovar_parent_info;
    private ArrayList<Tovar> tovar_child;
    private TovarDao tovarDao = new TovarDao();

    public List<Tovar> findAllTovar(){
        return tovarDao.findAll();
    }

    public Tovar findById(int id){
        return tovarDao.findById(id);
    }

    public void move_tovar(Tovar tovar,Tovar new_tovar_parent){
        int tovar_start_id = tovar.getId();
        int new_parent_id = new_tovar_parent.getId();
        tare = ((Double) tovar.getObject_info().get("Вес тары") + (Double) tovar.getObject_info().get("Вес тары узлов"));
        netto = ((Double) tovar.getObject_info().get("Вес нетто") + (Double) tovar.getObject_info().get("Вес нетто узлов"));
        if(tovar.getId_p() != 0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_with_old_info = tovarDao.findById(tovar.getId_p());
            tovar.setId_p(new_tovar_parent.getId());
            tovarDao.update(tovar);
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar.getId()){
                    tovar_child.remove(i);}}
            tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,-1*tare,-1*netto));
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;
            while (tovar.getId_p() !=0){
                tovar_parent = tovarDao.findById(tovar.getId_p());
                tovar_parent_info = tovar_parent.getObject_info();
                tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
                for(int i = 0; i < tovar_child.toArray().length;i++){
                    if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar_parent_with_old_info.getId()){
                        tovar_child.set(i,tovar);}}
                tovar_parent_with_old_info = tovar_parent;
                tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,-1*tare,-1*netto));
                tovarDao.update(tovar_parent);
                tovar = tovar_parent;}}
        tovar = tovarDao.findById(tovar_start_id);
        tovar_parent = tovarDao.findById(new_parent_id);
        tovar_parent_with_old_info = tovarDao.findById(new_parent_id);
        tovar_parent_info = tovar_parent.getObject_info();
        tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
        tovar_child.add(tovar);
        tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
        tovarDao.update(tovar_parent);
        tovar = tovar_parent;
        while (tovar.getId_p() !=0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar_parent_with_old_info.getId()){
                    tovar_child.set(i,tovar);}}
            tovar_parent_with_old_info = tovar_parent;
            tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;}}

    public void update_tovar_info(Tovar tovar,Map<String,Object> tovar_info){
        tovar = tovarDao.findById(tovar.getId());
        tare = (Double) tovar_info.get("Вес тары") - (Double) tovar.getObject_info().get("Вес тары");
        netto = (Double) tovar_info.get("Вес нетто") - (Double) tovar.getObject_info().get("Вес нетто");
        tovar.setObject_info(tovar_info);
        tovarDao.update(tovar);
        while (tovar.getId_p() != 0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar.getId()){
                    tovar_child.set(i,tovar);}}
            tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;
        }
    }

    public void update_tovar_info_cordinate(int id,Double x,Double y){
        Tovar tovar = tovarDao.findById(id);
        tovar.getObject_info().put("x",x);
        tovar.getObject_info().put("y",y);
        tovarDao.update(tovar);
        while (tovar.getId_p() != 0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar.getId()){
                    tovar_child.set(i,tovar);}}
            tovar_parent_info.put("Узлы", tovar_child);
            tovar_parent.setObject_info(tovar_parent_info);
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;
        }
    }

    public void saveTovar(Tovar tovar){
        tovarDao.save(tovar);
        if(tovar.getId_p() == 0){
            return;}
        tare = (Double) tovar.getObject_info().get("Вес тары");
        netto = (Double) tovar.getObject_info().get("Вес нетто");
        tovar_parent = tovarDao.findById(tovar.getId_p());
        tovar_parent_with_old_info = tovarDao.findById(tovar.getId_p());
        tovar_parent_info = tovar_parent.getObject_info();
        tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
        tovar_child.add(tovar);
        tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
        tovarDao.update(tovar_parent);
        tovar = tovar_parent;
        while (tovar.getId_p() !=0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar_parent_with_old_info.getId()){
                    tovar_child.set(i,tovar);}}
            tovar_parent_with_old_info = tovar_parent;
            tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;}}

    public void deleteTovar(Tovar tovar){
        deleteChild(tovar);
        if(tovar.getId_p() == 0){
            return;}
        tare = ((Double) tovar.getObject_info().get("Вес тары") + (Double) tovar.getObject_info().get("Вес тары узлов"))  * -1;
        netto = ((Double) tovar.getObject_info().get("Вес нетто") + (Double) tovar.getObject_info().get("Вес нетто узлов"))* -1;
        tovar_parent = tovarDao.findById(tovar.getId_p());
        tovar_parent_with_old_info = tovarDao.findById(tovar.getId_p());
        tovar_parent_info = tovar_parent.getObject_info();
        tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
        for(int i = 0; i < tovar_child.toArray().length;i++){
            if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar.getId()){
                tovar_child.remove(i);}}
        tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
        tovarDao.update(tovar_parent);
        tovar = tovar_parent;
        while (tovar.getId_p() !=0){
            tovar_parent = tovarDao.findById(tovar.getId_p());
            tovar_parent_info = tovar_parent.getObject_info();
            tovar_child = (ArrayList<Tovar>) tovar_parent_info.get("Узлы");
            for(int i = 0; i < tovar_child.toArray().length;i++){
                if(mapper.convertValue(tovar_child.get(i),Tovar.class).getId()==tovar_parent_with_old_info.getId()){
                    tovar_child.set(i,tovar);}}
            tovar_parent_with_old_info = tovar_parent;
            tovar_parent.setObject_info(change_info(tovar_parent_info,tovar_child,tare,netto));
            tovarDao.update(tovar_parent);
            tovar = tovar_parent;}}

    public void deleteChild(Tovar tovar){
        tovar_child = mapper.convertValue(tovar.getObject_info().get("Узлы"),new TypeReference<ArrayList<Tovar>>(){});
        tovarDao.delete(tovar);
        for(Tovar t: tovar_child){
            deleteChild(t);
        }
    }

    public List<Integer> getAllChildAndSelfId(Tovar tovar,List<Integer> result){
        tovar_child = mapper.convertValue(tovar.getObject_info().get("Узлы"),new TypeReference<ArrayList<Tovar>>(){});
        result.add(tovar.getId());
        for(Tovar t: tovar_child){
            getAllChildAndSelfId(t,result);
        }
        return  result;
    }

    public Map<String,Object> change_info(Map<String,Object> object_info,ArrayList<Tovar> tovar_child,Double tare,Double netto){
        object_info.put("Узлы",tovar_child);
        object_info.put("Вес тары узлов",(Double) object_info.get("Вес тары узлов")+ tare);
        object_info.put("Вес нетто узлов",(Double) object_info.get("Вес нетто узлов")+ netto);
        object_info.put("Вес бруто узлов",(Double) object_info.get("Вес бруто узлов")+ netto + tare);
        return object_info;
    }


}
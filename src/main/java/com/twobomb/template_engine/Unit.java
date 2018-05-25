package com.twobomb.template_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.twobomb.template_engine.Unit.Type.*;

class Unit {
    static public enum Type{VALUE,LIST,NOT_REQUIRED,ROOT,MAIN};

    private String ID;//Уникальный ид блока
    private Unit parent;
    private Type type;//Тип блока
    private String textblock;//Шаблон блока
    private int startPos;
    private int endPos;
    //для типа LIST
    private int _iterator;

    public Unit getParent() {
        return parent;
    }
    public Type getType() {
        return type;
    }
    public String getTextblock() {
        return textblock;
    }
    public String getTextblockWithoutOper() {
        if(type == MAIN)return textblock;
        return Pattern.compile("^\\s*"+addSlashes(getScope(type)[0])+"|"+addSlashes(getScope(type)[1])+"\\s*$",Pattern.DOTALL).matcher(textblock).replaceAll("");
    }
    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public boolean isHaveParent(Type t){//Есть ли родитель определенного типа
        Unit u = this;
        while (u.parent != null) {
            if(u.parent.getType() == t)
                return true;
            u = u.parent;
        }
            return false;
    }
    public String getText(HashMap<String,Object> rootmap){
        switch (type) {
            case MAIN: case ROOT: {
                String tmp = getTextblockWithoutOper();
                for (Unit child : getChildren()) {
                    tmp = tmp.replaceFirst(addSlashes(child.textblock), child.getText(rootmap));
                }
                return tmp; }
            case VALUE:
                String key = getTextblockWithoutOper().trim();
                if(isHaveParent(LIST)) {
                    Unit u = this;
                    List<Integer> indexes = new ArrayList<Integer>();

                    while (u.parent != null) {
                        if(u.parent.getType() == LIST)
                            indexes.add(0,u.parent._iterator);
                        u = u.parent;
                    }

                    List<Object> list = (List)getValueAsKey(rootmap,key);
                    for (int i = 0; i < indexes.size();i++)
                        if(i == indexes.size()-1) {
                            String str = (String)list.get(indexes.get(i));
                            return str;
                        }
                        else
                            list = (List)list.get(indexes.get(i));


                }
                else return (String)getValueAsKey(rootmap,key);
            case NOT_REQUIRED:{
                String tmp = getTextblockWithoutOper();
                for (Unit child : getChildren()) {
                    String newval = child.getText(rootmap);
                    if(newval == null || newval.equals(""))
                        return "";
                    else
                        tmp = tmp.replaceFirst(addSlashes(child.textblock), newval);
                }
                return tmp;}
            case LIST:
                Unit u = this;
                List<Integer> indexes = new ArrayList<Integer>();
                while (u.parent != null) {
                    if(u.parent.getType() == LIST)
                        indexes.add(0,u.parent._iterator);
                    u = u.parent;
                }
                List<Object> list  = (List)getValueAsKey(rootmap,findFirstChild(this,VALUE).getTextblockWithoutOper());
                int cnt = 0;
                for(int i = 0; i < indexes.size();i++)
                        list = (List)list.get(indexes.get(i));
                cnt = list.size();
                String tmp = "";
                for(_iterator = 0; _iterator < cnt;_iterator++) {
                    String tmpTmpl = getTextblockWithoutOper();
                    for(Unit child:getChildren()) {
                        tmpTmpl= tmpTmpl.replaceFirst(addSlashes(child.textblock),child.getText(rootmap));
                    }
                    tmp+=tmpTmpl;
                }
                return tmp;
                }

        return "";
    }
    public Unit findFirstChild(Unit u,Type type){
        for(Unit child:u.getChildren())
            if(child.getType() == type)
                return child;
            else if(child.getChildren().size() > 0) {
                if (findFirstChild(child, type) != null)
                    return findFirstChild(child, type);
            }
        return null;
    }
    public String getListId(){
        Unit main = getMainBlock();
        return getListId(main,this,"");
    }
    static boolean isExists(HashMap<String,Object> map , String keys){//Проверяет существует ли такая ветка ключей ,keys  - value.value1.value2
        String[] values = keys.split("\\.");
        for(int i = 0; i < values.length;i++)
            if(map.containsKey(values[i].trim())) {
                if (i == values.length - 1)
                    return true;
                else if (!(map.get(values[i].trim()) instanceof HashMap))
                    return false;
                else
                    map = (HashMap) map.get(values[i].trim());
            }
            else
                return false;
        return false;
    }
    static Object getValueAsKey(HashMap<String,Object> map ,String keys){//Возвращает значение по векте ключей,keys  - value.value1.value2
        if(!isExists(map,keys))return null;
        String[] values = keys.split("\\.");
        for(int i = 0; i < values.length-1;i++)
            map = (HashMap) map.get(values[i].trim());
        return map.get(values[values.length-1].trim());
    }
    public String getListId(Unit m,Unit list,String id){//Получить ид листа
        if(m.eq(list))
            return id;
        int id_list = 0;
        if(m.getType() == ROOT)
            for(Unit root: m.parent.getChildren()) {
                if(root.eq(m)) break;
                for (Unit c : root.getChildren())
                    if (c.getType() == LIST)
                        id_list++;
            }
        for(int i = 0; i < m.getChildren().size();i++){
            String t =getListId(m.getChildren().get(i), list,  m.getChildren().get(i).type== LIST?id  + id_list:id);
            if(t!=null)return t;
            if(m.getChildren().get(i).type== LIST)
                id_list++;
        }
        return null;
    }
    public Unit getMainBlock(){
        Unit m = this;
        while (m.type != MAIN)
            m = m.parent;
        return m;
    }
    public static int getCountDuplicate(String source,String regex){//Кол-во повторений слова в тексте
        Matcher m = Pattern.compile(regex).matcher(source);
        int i = 0;
        while(m.find())
            i++;
        return i;
    }
    public static boolean isValidSyntax(String str) throws Exception {
        if(getCountDuplicate(str,"\\$")%2 != 0)
            throw new Exception("Неверный синтаксис, не хватает $!");
        List<Unit> roots = new Unit(str,0,0,MAIN,"0").getChildren();
        for (int i = 0;i < roots.size();i++){//Добавить проверку на [] в блоках []
            if(getCountDuplicate(roots.get(i).textblock,"\\{") != getCountDuplicate(roots.get(i).textblock,"\\}"))
                throw new Exception("Неверный синтаксис, не хватает '{' или '}' в "+(i+1)+" блоке. Блок: "+roots.get(i).textblock);
            if(getCountDuplicate(roots.get(i).textblock,"\\[") != getCountDuplicate(roots.get(i).textblock,"\\]"))
                throw new Exception("Неверный синтаксис, не хватает '[' или ']' в "+(i+1)+" блоке. Блок: "+roots.get(i).textblock);
            if(getCountDuplicate(roots.get(i).textblock,"\\<") != getCountDuplicate(roots.get(i).textblock,"\\>"))
                throw new Exception("Неверный синтаксис, не хватает '<' или '>' в "+(i+1)+" блоке. Блок: "+roots.get(i).textblock);
        }
        return true;
    }
    public static Unit Instance(String pattern) throws Exception {
        if(!isValidSyntax(pattern))
            return null;

        return new Unit(pattern,0,pattern.length()-1,MAIN,"0");
    }
    private void setID(String id){
        if(parent != null)
            this.ID = parent.ID+id;
        else
            this.ID = id;
    }
    private Unit(String textblock, int startPos, int endPos,Type t,String id) {
        this.textblock = textblock;
        this.startPos = startPos;
        this.endPos = endPos;
        this.type = t;
        this.ID = id;
    }

    List<Unit> getChildren(){
        switch (type){
            case MAIN:return searchAllBlock(getTextblockWithoutOper(),ROOT,this);
            case ROOT: return searchChildren(getTextblockWithoutOper(), new Type[]{LIST, VALUE, NOT_REQUIRED},this);
            case LIST:return searchChildren(getTextblockWithoutOper(), new Type[]{LIST, VALUE, NOT_REQUIRED},this);
            case NOT_REQUIRED: return searchChildren(getTextblockWithoutOper(), new Type[]{VALUE},this);
            case VALUE: return new ArrayList<Unit>();
        }
        return null;
    }
    private  List<Unit> searchChildren(String txt, Type[] needTypes, Unit parent){
        List<Unit> s = searchChildren(txt,needTypes);
        for(Unit u:s) {
            u.parent = parent;
            u.setID(u.ID);
        }
        return s;
    }
    private  List<Unit> searchChildren(String txt, Type[] needTypes){
        List<Unit> u = new ArrayList<Unit>();
        Type root = null;
        int ignore = 0;
        int startPos = 0;
        int id = 0;
        for(int i = 0; i < txt.length();i++){
            if(root == null) {
                for (int j = 0; j < needTypes.length; j++)
                    if (String.valueOf(txt.charAt(i)).equals(getScope(needTypes[j])[0])) {
                        root = needTypes[j];
                        startPos = i;
                        break;
                    }
            }
            else if(String.valueOf(txt.charAt(i)).equals(getScope(root)[0]))
                ignore++;
            else if(String.valueOf(txt.charAt(i)).equals(getScope(root)[1])){
                if(ignore > 0){
                    ignore--;
                    continue;
                }
                else{
                    u.add( new Unit(txt.substring(startPos,i+1),startPos,i,root,String.valueOf(id++)));
                    root = null;

                }
            }
        }
        return u;
    }
     private String[] getScope(Type t){
        switch (t){
            case ROOT:return new String[]{"$", "$"};
            case LIST:return new String[]{"[", "]"};
            case VALUE:return new String[]{"{", "}"};
            case NOT_REQUIRED:return new String[]{"<", ">"};
        }
        return null;
    }
    static String addSlashes(String s){
        String[] words = {"{", "}", "[", "]","$","^"};
        for(String w:words)
            s = s.replace(w,"\\"+w);
        return s;
    }
    public boolean eq(Unit e){
        return e.ID.equals(ID);
    }
     private List<Unit> searchAllBlock(String textblock,Type t,Unit parent){
         List<Unit> u = searchAllBlock(textblock,t);
         for(Unit s:u) {
             s.parent = parent;
             s.setID(s.ID);
         }
         return u;
     }
     public List<Unit> searchAllBlock(String textblock, Type t){
         String ss = getScope(t)[0];
         String se = getScope(t)[1];
        Matcher m = Pattern.compile("([\\"+ss+"][^\\"+se+"]*[\\"+se+"])").matcher(textblock);
        List<Unit> units = new ArrayList<Unit>();
        int id = 0;
        while (m.find())
            units.add(new Unit(m.group(1), m.start(), m.end() - 1, t, String.valueOf(id++)));
        return units;
    }
}

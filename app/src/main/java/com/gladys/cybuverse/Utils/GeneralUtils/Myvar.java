package com.gladys.cybuverse.Utils.GeneralUtils;

import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.repeatString;

//interface Command{
//    void function ();
//}

@RequiresApi(api = Build.VERSION_CODES.N)
public class Myvar implements Cloneable {

    private Object obj;
    private Object[] arrobj;
    private boolean BoolStatus = false;
    private boolean wlooperStatus = false;
    private int looper = 0;
    private String isRun = "";
    private Map<String, Object> varcontainer = new HashMap<>();
    private Map<String, Object[]> arrcontainer = new HashMap<>();
    private Funcs.Printer MyPrinter = new Funcs.Printer();

    public Myvar() {
    }

    public Myvar(Object value) {
        setValue(value);
    }

    public Myvar(Object[] value) {
        setValue(value);
    }

    public Myvar(ArrayList value) {
        setValue(value.toArray());
    }

    public Myvar setValue(Object obj) {
        this.obj = obj;
        arrobj = null;
        return this;
    }

    public Myvar setValue(Object[] obj) {
        this.arrobj = obj;
        obj = null;
        return this;
    }

    public Myvar setValue(ArrayList obj) {
        this.arrobj = obj.toArray();
        obj = null;
        return this;
    }

    public String getType() {
        return (this.obj == null) ? this.arrobj.getClass().getSimpleName() : this.obj.getClass().getSimpleName();
    }

    private void on() {
        this.BoolStatus = true;
    }

    private void off() {
        this.BoolStatus = false;
    }

    private boolean isOn() {
        return this.BoolStatus;
    }

    private boolean whileOn() {
        return this.wlooperStatus = true;
    }

    private boolean whileOff() {
        return this.wlooperStatus = false;
    }

    private boolean iswhileOn() {
        return this.wlooperStatus;
    }

    public Myvar format(Object... with) {
        return this.setValue(Funcs.format(this.toString(), with));
    }

    public Object toObject() {
        return this.obj;
    }

    public String toString() {
        return Funcs.toStrings(this.obj);
    }

    public Float toFloat() {
        return Funcs.toFloat(this.obj);
    }

    public Character toCharacter() {
        return Funcs.toCharacter(this.obj);
    }

    public Integer toInteger() {
        return Funcs.toInteger(this.obj);
    }

    public Double toDouble() {
        return Funcs.toDouble(this.obj);
    }

    public Boolean toBoolean() {
        return Funcs.toBoolean(this.obj);
    }

    public Object[] toArrObject() {
        return this.arrobj;
    }

    public String[] toArrString() {
        return Funcs.toStrings(this.arrobj);
    }

    public Float[] toArrFloat() {
        return Funcs.toFloat(this.arrobj);
    }

    public Character[] toArrCharacter() {
        return Funcs.toCharacter(this.arrobj);
    }

    public Integer[] toArrInteger() {
        return Funcs.toInteger(this.arrobj);
    }

    public Double[] toArrDouble() {
        return Funcs.toDouble(this.arrobj);
    }

    public Boolean[] toArrBoolean() {
        return Funcs.toBoolean(this.arrobj);
    }

    public Myvar clone() {
        try {
            return (Myvar) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Myvar reverse() {
        if (getType().equals("String")) {
            this.setValue(Funcs.reverse(this.obj.toString()));
        } else if (getType().contains("[]")) {
            this.setValue(Funcs.reverse(this.arrobj));
        }
        return this;
    }

    public Myvar split(Object obj) {
        return this.setValue(Funcs.split(this.obj, obj));
    }

    public int hashCode() {
        return (this.obj == null) ? this.arrobj.hashCode() : this.obj.hashCode();
    }

    public boolean equals(Object obj) {
        return (this.obj == null) ? this.arrobj.equals(obj) : this.obj.equals(obj);
    }

    public int length() {
        return (this.obj == null) ? this.arrobj.toString().length() : this.obj.toString().length();
    }

    public Myvar endl() {
        print("\n");
        return this;
    }

    public Myvar endl(Integer obj) {
        print(repeatString("\n", obj));
        return this;
    }

    public Myvar tab() {
        print("\t");
        return this;
    }

    public Myvar tab(Integer obj) {
        print(repeatString("\t", obj));
        return this;
    }

    public Myvar printer(String start, String sep, String end) {
        MyPrinter.start(start);
        MyPrinter.sep(sep);
        MyPrinter.end(end);
        return this;
    }

    public Myvar print() {
        if (this.obj != null) {
            println(this);
        } else {
            for (Object i : arrobj) {
                println(i);
            }
        }
        return this;
    }

    public Myvar print(Object... objs) {
        for (Object obj : objs) {
            if (obj.getClass().getSimpleName().contains("[]")) {
                Funcs.print(obj);
            } else {
                MyPrinter.print(obj);
            }
        }
        return this;
    }

    public Myvar println(Object... objs) {
        for (Object obj : objs) {
            if (obj.getClass().getSimpleName().contains("[]")) {
                Funcs.println(obj);
            } else {
                MyPrinter.print(obj);
                MyPrinter.print();
            }
        }
        return this;
    }

    //Object obj
    public Myvar add(Object obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            return this.setValue(this.obj.toString().concat(obj.toString()));
        }
        return this.setValue(this.toFloat() + Funcs.toDouble(obj));
    }

    public Myvar subtract(Object obj) {
        return this.setValue(this.toDouble() - Funcs.toDouble(obj));
    }

    public Myvar divide(Object obj) {
        return this.setValue(this.toDouble() / Funcs.toDouble(obj));
    }

    public Myvar multiply(Object obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            return this.setValue(repeatString(this.obj.toString(), Funcs.toInteger(obj)));
        }
        return this.setValue(this.toDouble() * Funcs.toDouble(obj));
    }

    public Myvar mod(Object obj) {
        return this.setValue(this.toFloat() % Funcs.toDouble(obj));
    }

    public Myvar pow(Object obj) {
        return this.setValue(Funcs.power(this.obj, obj));
    }

    //int obj
    public Myvar add(int obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            Object Obj = obj;
            return this.setValue(this.obj.toString().concat(Obj.toString()));
        }
        return this.setValue(this.toDouble() + obj);
    }

    public Myvar subtract(int obj) {
        return this.setValue(this.toDouble() - obj);
    }

    public Myvar divide(int obj) {
        return this.setValue(this.toDouble() / obj);
    }

    public Myvar multiply(int obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            return this.setValue(repeatString(this.obj.toString(), obj));
        }
        return this.setValue(this.toDouble() * obj);
    }

    public Myvar modulus(int obj) {
        return this.setValue(this.toDouble() % obj);
    }

    public Myvar power(int obj) {
        return this.setValue(Funcs.power(this.obj, obj));
    }

    //float obj
    public Myvar add(float obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            Object Obj = obj;
            return this.setValue(this.obj.toString().concat(Obj.toString()));
        }
        return this.setValue(this.toDouble() + obj);
    }

    public Myvar subtract(float obj) {
        return this.setValue(this.toDouble() - obj);
    }

    public Myvar divide(float obj) {
        return this.setValue(this.toDouble() / obj);
    }

    public Myvar multiply(float obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            return this.setValue(repeatString(this.obj.toString(), (int) obj));
        }
        return this.setValue(this.toDouble() * obj);
    }

    public Myvar modulus(float obj) {
        return this.setValue(this.toDouble() % obj);
    }

    public Myvar power(float obj) {
        return this.setValue(Funcs.power(this.obj, obj));
    }

    //double obj
    public Myvar add(double obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            Object Obj = obj;
            return this.setValue(this.obj.toString().concat(Obj.toString()));
        }
        return this.setValue(this.toDouble() + obj);
    }

    public Myvar subtract(double obj) {
        return this.setValue(this.toDouble() - obj);
    }

    public Myvar divide(double obj) {
        return this.setValue(this.toDouble() / obj);
    }

    public Myvar multiply(double obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            return this.setValue(repeatString(this.obj.toString(), (int) obj));
        }
        return this.setValue(this.toDouble() * obj);
    }

    public Myvar modulus(double obj) {
        return this.setValue(this.toDouble() % obj);
    }

    public Myvar power(double obj) {
        return this.setValue(Funcs.power(this.obj, obj));
    }

    //char obj
    public Myvar add(char obj) {
        if (this.obj.getClass().getSimpleName().equals("String")) {
            Object Obj = obj;
            return this.setValue(this.obj.toString().concat(Obj.toString()));
        }
        Object Obj = obj;
        return this.setValue(Funcs.toDouble(this.obj) + Funcs.toDouble(Obj));
    }

    public Myvar inputInt(String show) {
        return this.setValue(Funcs.inputInt(show));
    }

    public Myvar getStr(String show) {
        return this.setValue(Funcs.getStr(show));
    }

    public Myvar inputStr(String show) {
        return this.setValue(Funcs.inputStr(show));
    }

    public Myvar inputFloat(String show) {
        return this.setValue(Funcs.inputFloat(show));
    }

    public Myvar inputDouble(String show) {
        return this.setValue(Funcs.inputDouble(show));
    }

    public Myvar inputChar(String show) {
        return this.setValue(Funcs.inputChar(show));
    }

    public Myvar inputBool(String show) {
        return this.setValue(Funcs.inputBool(show));
    }

    public Myvar random() {
        return this.setValue(Funcs.random());
    }

    public Myvar randint() {
        return this.setValue(Funcs.randint());
    }

    public Myvar randint(int max) {
        return this.setValue(Funcs.randint(max));
    }

    public Myvar randrange(int size) {
        return this.setValue(Funcs.randrange(size));
    }

    public Myvar randrange(int max, int size) {
        return this.setValue(Funcs.randrange(max, size));
    }

    public Object[] randsample(int sample_size) {
        return Funcs.randsample(arrobj, sample_size);
    }

    public Myvar setConsoleLength(Integer value) {
        MyPrinter.setConsoleLength(value);
        return this;
    }


    //##### Console Text Manipulatuon #####

    public Myvar alignNormal() {
        MyPrinter.alignNormal();
        return this;
    }

    public Myvar alignCenter() {
        MyPrinter.alignCenter();
        return this;
    }

    public Myvar alignLeft() {
        MyPrinter.alignLeft();
        return this;
    }

    public Myvar alignRight() {
        MyPrinter.alignRight();
        return this;
    }

    public Myvar alignReset() {
        MyPrinter.resetAlignment();
        return this;
    }

    public Myvar andprint(Object obj) {
        print(repeatString(obj.toString(), looper));
        looper = 0;
        return this;
    }

    public Myvar andrun(Command command) {
        for (int i = 0; i < looper; i++) {
            command.function();
        }
        looper = 0;
        return this;
    }

    public Myvar andtotal(String show) {
        if (looper != 0) {
            for (int i = 0; i < looper; i++) {
                this.obj = this.toDouble() + Funcs.inputDouble(show);
            }
            looper = 0;
            return this;
        }
        if (iswhileOn()) {
            Object num;
            while (iswhileOn()) {
                num = Funcs.inputStr(show);
                if (num.toString().equals(":q")) {
                    this.obj = this.toDouble() + Funcs.toDouble(num);
                } else {
                    whileOff();
                }
            }
        }
        return this;
    }

    public Myvar andsubtract(String show) {
        for (int i = 0; i < looper; i++) {
            this.obj = this.toDouble() - Funcs.inputDouble(show);
        }
        looper = 0;
        return this;
    }

    public Myvar andmultiply(String show) {
        for (int i = 0; i < looper; i++) {
            this.obj = this.toDouble() * Funcs.inputDouble(show);
        }
        looper = 0;
        return this;
    }

    public Myvar anddivide(String show) {
        for (int i = 0; i < looper; i++) {
            this.obj = this.toDouble() / Funcs.inputDouble(show);
        }
        looper = 0;
        return this;
    }

    public Myvar isnull() {
        if (this.obj == null && this.arrobj == null) {
            this.on();
        }
        this.isRun = "ready";
        return this;
    }

    public Myvar isequal(Object obj) {
        if (obj != null) {
            if ((obj.hashCode() == (this.obj.hashCode()))) {
                this.on();
            }
            this.isRun = "ready";
            return this;
        } else {
            if ((obj.equals(this.arrobj.hashCode()))) {
                this.on();
            }
            this.isRun = "ready";
            return this;
        }
    }

    public Myvar isnot_equal(Object obj) {
        if (obj != null) {
            if (!(obj.hashCode() == (this.obj.hashCode()))) {
                this.on();
            }
            this.isRun = "ready";
            return this;
        } else {
            if (!(obj.equals(this.arrobj.hashCode()))) {
                this.on();
            }
            this.isRun = "ready";
            return this;
        }
    }

    public Myvar isgreater(Object obj) {
        if ((obj.hashCode() < (this.obj.hashCode()))) {
            this.on();
        }
        this.isRun = "ready";
        return this;
    }

    public Myvar islesser(Object obj) {
        if ((obj.hashCode() > (this.obj.hashCode()))) {
            this.on();
        }
        this.isRun = "ready";
        return this;
    }

    public Myvar isgreater_or_equal(Object obj) {
        if ((obj.hashCode() <= (this.obj.hashCode()))) {
            this.on();
        }
        this.isRun = "ready";
        return this;
    }

    public Myvar islesser_or_equal(Object obj) {
        if ((obj.hashCode() >= (this.obj.hashCode()))) {
            this.on();
        }
        this.isRun = "ready";
        return this;
    }

    public boolean existVar(String varname) {
        //check if var exist
        return varcontainer.containsKey(varname);
    }

    //##### Variable Management #####

    public Myvar assign(String varname, Object obj) {
        if (!existVar(varname)) {
            varcontainer.putIfAbsent(varname, obj);
        } else {
            varcontainer.replace(varname, obj);
        }
        return this;
    }

    public Object getVar(String varname) {
        if (existVar(varname)) {
            return varcontainer.get(varname);
        }
        return new Object();
    }

    public Myvar delVar(String varname) {
        if (existVar(varname)) {
            varcontainer.remove(varname);
        }
        return this;
    }

    public Myvar clearVars() {
        varcontainer.clear();//empty variable from variable map
        return this;
    }

    private String listItems(Object[] arrayVar, Integer... varOnRow) {
        Integer onRow;
        if (varOnRow.length == 1) {
            onRow = varOnRow[0];
        } else {
            onRow = 3;
        }
        StringBuilder string_arr = new StringBuilder("\nvalues   : \n\t\t\t");
        for (int i = 0; i < arrayVar.length; i++) {
            string_arr.append(arrayVar[i]).append(((i + 1) % onRow == 0) ? "\n\t\t\t" : "\t\t");
        }
        return string_arr + "\n";
    }

    public Myvar listVars() {
        new Funcs.MapForEach<String, Object>(varcontainer, new Funcs.MapAction<String, Object>() {
            @Override
            public void operate(String key, Object value) {
                print("\nvaraible :\t", key, "\nvalue    :\t", value, "\n");
            }
        });
        return this;//empty variable from variable map
    }

    public Myvar listArrs() {
        new Funcs.MapForEach<String, Object[]>(arrcontainer, new Funcs.MapAction<String, Object[]>() {
            @Override
            public void operate(String key, Object[] value) {
                print("variable : ", key, " list", listItems(value));
            }
        });
        return this;//empty variable from variable map
    }

    public Myvar listArrs(final int varOnRow) {
        new Funcs.MapForEach<String, Object[]>(arrcontainer, new Funcs.MapAction<String, Object[]>() {
            @Override
            public void operate(String key, Object[] value) {
                print("variable : ", key, " list", listItems(value, varOnRow));
            }
        });
        return this;//empty variable from variable map
    }

    public Map<String, Object> getVarContainer() {
        return varcontainer;
    }

    public Map<String, Object[]> getArrContainer() {
        return arrcontainer;
    }

    public Myvar increamentVar(String varname) {
        if (existVar(varname)) {
            Object new_ = Funcs.toDouble(getVar(varname)) + 1;
            varcontainer.replace(varname, getVar(varname), new_);
        }
        return this;//++ variable from variable map
    }

    public Myvar increamentVar(String varname, int by) {
        if (existVar(varname)) {
            Object new_ = Funcs.toDouble(getVar(varname)) + by;
            varcontainer.replace(varname, getVar(varname), new_);
        }
        return this;//++by variable from variable map
    }

    public Myvar decrementVar(String varname) {
        if (existVar(varname)) {
            Object new_ = Funcs.toDouble(getVar(varname)) - 1;
            varcontainer.replace(varname, getVar(varname), new_);
        }
        return this;//-- variable from variable map
    }

    public Myvar decrementVar(String varname, int by) {
        if (existVar(varname)) {
            Object new_ = Funcs.toDouble(getVar(varname)) - by;
            varcontainer.replace(varname, getVar(varname), new_);
        }
        return this;//--by variable from variable map
    }

    public boolean existArr(String varname) {
        //check if var exist
        return arrcontainer.containsKey(varname);
    }

    public Myvar assign(String varname, Object[] obj) {
        if (!existVar(varname)) {
            arrcontainer.putIfAbsent(varname, obj);
        } else {
            arrcontainer.replace(varname, obj);
        }
        return this;
    }

    public Object[] getArr(String varname) {
        if (existArr(varname)) {
            return arrcontainer.get(varname);
        }
        return new Object[0];
    }

    public Myvar delArr(String varname) {
        if (existArr(varname)) {
            arrcontainer.remove(varname);
        }
        return this;
    }

    public Myvar clearArrs() {
        arrcontainer.clear();
        return this;//empty variable from variable map
    }

    public Myvar when(Object function, boolean result) {
        isRun = "ready";
        if (result == Funcs.toBoolean(function)) {
            on();
        } else {
            off();
        }
        return this;
    }

    //##### Process Flows #####

    public Myvar when(boolean result) {
        isRun = "ready";
        BoolStatus = result;
        return this;
    }

    public Myvar elif(Object function, boolean result) {
        if (!isOn()) {
            if (result == Funcs.toBoolean(function)) {
                on();
            } else {
                off();
            }
        }
        return this;
    }

    public Myvar elif(boolean result) {
        BoolStatus = result;
        return this;
    }

    public Myvar orelse(Command usercommand) {
        if (!isOn() && isRun.equals("ready")) {
            on();
            usercommand.function();
            isRun = "pass";
        }
        off();
        return this;
    }

    public Myvar loop(int obj) {
        if (looper == 0) {
            this.looper = obj;
        } else {
            print("\nloop is still on : " + looper + " runs left.\n");
        }
        return this;
    }

    public void loopwhile() {
        whileOn();
    }

    public Myvar run(Command usercommand) {
        if (isOn() && isRun.equals("ready")) {
            usercommand.function();
            isRun = "pass";
            off();
        }
        return this;
    }

    public Myvar dothis(Command usercommand) {
        usercommand.function();
        return this;
    }

    public static abstract class Command {
        public abstract void function();
    }

}

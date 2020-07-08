package com.gladys.cybuverse.Utils.GeneralUtils;


import com.gladys.cybuverse.Utils.GeneralUtils.collections.Stack;

public class Main {

    public static void main(String[] args) throws Exception {
        // write your code here
//        print(reverse("mommy"), '\n');
//        String[] names = {"dssf", "Ben", "Wwefs", "sfgsfg"};
//        Integer[] nums = {1,2,3,4,5,6,7,8};
//        nums = toInteger(reverse(nums));
//        listItems(nums, 0);
//        print(findIn(names, "ben", false));
//        print(findIn(names, "ben", true));
//        print(findIn(nums, 8));
//        print(1 == 4 ? "\now yeah" : "\nboooh");
//        String name = "\nAtinga <d> Bernard <2> <1> <D> <tz> on <DT>";
//        print(name.toLowerCase());
//        print("\nHello Word\t");print(1,3,4,5,6);
//        print(format("\n<>\n", new Random().nextInt(100)));
//        print("\nhello ", "sturpid ", "universe\n");
//        print(name);
//        print(format(name, "Azumah", "Awinyel", "Mercy", "gffhyhjgj"));
//        print("\n", datetime(true, name));
//        print(format(datetime(true, name), "Azumah", "Awinyel", "Mercy", "gffhyhjgj"));
//        print(format(name, 546.565,"fgdfg",454));

//        print("\n",datetime());
//        print("\n",date());
//        print("\n",time(),"\n");
//        print("\n",datetime("M dn y"));
//        print("\n",datetime("dn y"));
//        print("\n",datetime("h m s"));
//        print("\n",datetime("dn","day","month","year"));
//
//        println("\n",datetime(true, "todays date is <dn>th <M>, <y>"));
//        println("\n",datetime(true, "<h> : <m>"));
//        println("\n",datetime(true, "<h>:<m>"));
//        print("\n", datetime("second"));
//        print("\n", datetime("hour"));
//
//        listItems(names, 2);
//        listItems(nums, 2, "ODDS", "Evens");
//        print(getStr("\n( line ) whats your name : "));
//        print(inputStr("\n( word ) whats your gender : "));
//        print(inputChar("\n( char ) you like sex (y/n) : "));
//        print(inputInt("\n( int ) whats your age : "));
//        print(inputBool("\n( bool ) would toy say you're young : "));
//        print(inputFloat("\n( float ) how much would you like for the loan : "));
//        print(inputDouble("\n( double ) what would be you interest rate : "));

//        TestIOFuncs.IOStreams x = new TestIOFuncs.IOStreams();
//
//        x.make();
//
//        x.input();
//
//        try {
//            x.writeUTF("gfdgdff ghfdhgfhg gd");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        x.file();
//
//        x.fileread();
//
//        x.mkdir("/");
//
//        x.ls("/");


//                Myvar x = new Myvar();
//
//                x.isnull()
//                        .run(new Command() {
//                            @Override
//                            public void function() {
//                                x.inputInt("\nwhat is the name for this val : ");
//                            }
//                        }).isgreater(20).run(new Command() {
//                            @Override
//                            public void function() {
//                                print("\nthis");
//                            }
//                        })
//                        .orelse(new Command() {
//                            @Override
//                            public void function() {
//                                print("\nthat");
//                            }
//                        })
//                        .endl()
//                        .setValue(15)
//                        .isequal(inputInt("enter a number : "))
//                        .run(new Command() {
//                            @Override
//                            public void function() {
//                                print("that was true");
//                            }
//                        })
//                        .orelse(new Command() {
//                            @Override
//                            public void function() {
//                                print("\nthat was false");
//                            }
//                        })
//                        .dothis(new Command() {
//                            @Override
//                            public void function() {
//                                print("\ni own this bitch...who the fuc are u\n");
//                                while(x.toInteger() < 20){
//                                    x.inputInt("enter another number : ");
//                                }
//                                print("\nowkay you passed");
//                            }
//                        })
//                        .endl(2)
//                        .loop(60).andprint("#")
//                        .endl()
//                        .println("the value of v is ", x.toInteger())
//                        .endl()
//                        .loop(60).andprint("#");
//


//        class UserInfo{
//            String name;
//            Integer age;
//            String gender;
//            String dob;
//            String Residence;
//            String Occupation;
//            String Level;
//            String Course;
//            Float Salary;
//            String Hobby;
//            String S_D_Status;
//        }
//
//
//        List<UserInfo> UsersList = new ArrayList<UserInfo>();
//
//        //program to get the user information
//
//            Myvar v = new Myvar();
//            v.endl().loop(60).andprint("#")
//                    .println(alignCenter("\nUser Information Manager\n",60))
//                    .loop(60).andprint("#").endl(2)
//                    .assign("name", getStr("Enter user name \t\t:\t"))
//                    .assign("age", inputInt("Enter user age  \t\t:\t"))
//                    .assign("gender", inputChar("Enter user gender (m/f)\t:\t"))
//                    .assign("dob", getStr("Enter user DOB (d m y) \t:\t"))
//                    .assign("residence", getStr("Enter user Residence \t:\t"))
//                    .assign("job", getStr("Enter user job Status \t:\t"))
//                    .setValue(v.getVar("job"))
//                    .when(v.toString().equalsIgnoreCase("student"))
//                    .run(new Command() {
//                        @Override
//                        public void function() {
//                            v.assign("school", getStr("user School and level \t:\t"))
//                            .assign("course", getStr("Enter course offering \t:\t"));
//                        }
//                    })
//                    .orelse(new Command() {
//                        @Override
//                        public void function() {
//                            v.assign("position", inputStr("Enter job position \t\t:\t"))
//                            .assign("salary", inputFloat("Enter user salary \t\t:\t"));
//                        }
//                    })
//                    .assign("hobby", inputStr("What the users hobby \t:\t"))
//                    .assign("sds", inputStr("Does user smoke / drink :\t"))
//                    .endl(2).loop(60).andprint("*").endl().loop(60).andprint("*").endl()
//                    .println(alignCenter("Displaying user details", 60)).endl().loop(60).andprint("#")
//                    .endl().listVars().endl().loop(60).andprint("*").endl(2)
//                    .inputStr("Would you like to change any use detail (y/n) : ")
//                    .when(v.toString().equalsIgnoreCase("y"))
//                    .run(new Command() {
//                        @Override
//                        public void function() {
//                            while (true){
//                                String ans = inputStr("\n(use the var names above or enter q to quit) \nwhat would yo like to change :\t");
//                                if (ans.equals("q")){
//                                    break;
//                                }
//                                else{
//                                    if (v.existVar(ans)){
//                                        print("\ncurrent value is\t", v.getVar(ans).toString());
//                                        v.assign(ans, getStr("\nchange to :\t")).endl();
//                                    }
//                                }
//                                v.listVars();
//                            }
//                        }
//                    })
//                    .dothis(new Command() {
//                        @Override
//                        public void function() {
//                            UserInfo obj = new UserInfo();
//                            obj.name = v.getVar("name").toString();
//                            obj.age = toInteger(v.getVar("age"));
//                            obj.gender = v.getVar("gender").toString();
//                            obj.dob = v.getVar("dob").toString();
//                            obj.Residence = v.getVar("residence").toString();
//                            obj.Hobby = v.getVar("hobby").toString();
//                            obj.S_D_Status = v.getVar("sds").toString();
//                            if (v.getVar("job").toString().equalsIgnoreCase("student")){
//                                obj.Level = v.getVar("school").toString();
//                                obj.Course = v.getVar("course").toString();
//                            }else{
//                                obj.Salary = toFloat(v.getVar("salary"));
//                                obj.Occupation = v.getVar("job").toString();
//                            }
//                            UsersList.put(obj);
//                        }
//                    })
//                    .endl(2).loop(60).andprint("#").endl()
//                    .println(alignCenter("End of Progam",20))
//                    .endl().loop(60).andprint("#").endl(2)
//                    .clearVars();
//
//
//                    print(UsersList.get(0).name, " likes ", UsersList.get(0).Hobby);


//                Myvar v = new Myvar();
//                    v.loop(60)
//                    .andprint("*").endl()
//                    .print(alignCenter("Welcome to the Story of a young boy who knows he freaking wantsmywhatall\n i want is toooon baby tell me if i kie baby\n the great Atinga \nBernard" ,60))
//                    .endl(2).loop(60).andprint("*").endl().loop(60).andprint("*")
//                    .endl().print(alignRight("\n\nJubille Academy Sch,\nP.O.Box 567,\nAshiaman.\n20th Dec 2019.\n\n",60))
//                    .print(alignLeft("Mr Headmaster,\nP.O.Box 67\nTema - 55.",60)).endl(2)
//                    .print(alignLeft("Dear Sir,",60)).endl()
//                    .print(alignCenter("Letter to say my mind",60))
//                    .endl().print(alignCenter("..........................",60)).endl()
//                    .tab().print(alignLeft("My name is bernard atinga and i created and implemented all this text processor functions so suck on that.",60)).endl()
//                    .print(alignLeft("if you need me you know what to do.",60)).endl(2)
//                    .print(alignRight("Yours Awesome\n.............\nOtc Chingy",60)).endl();


//        int v = inputInt("default");
//        print(v);
//        float x = inputFloat("default");
//        print(x);
//        double y = inputDouble("default");
//        print(y);
//        String z = inputStr("default");
//        print(z);
//
//        var square = new raise_to_power(2);
//        var tenth = new raise_to_power(10);
//        var fifth = new raise_to_power(5);
//        println(square.num(10));
//        println(fifth.num(4));
//        println(tenth.num(4));
//
//        println(squareRoot(3));
//        println(cubeRoot(2));


//        print(randint(),"\n");
//        println(randint(5),"\n");
//        for (int i : range(10)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : range(10, 20)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : range(10, 20,4)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : randrange(10)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : randrange(10,15)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : randrange(10,30,10)){
//            println(i);
//        }
//        print("\n\n");
//
//        Integer[] nums = {12,4,2,65,3,56,99,345,5,3,5,2,45,5,46,5};
//        String[] names = {"bernard", "azumah", "chris", "noah", "joshua", "benson", "kwame", "bless", "rex", "jacob"};
//
//        for (Object i : randsample(nums,7)){
//            println(i);
//        }
//        print("\n\n");
//        for (int i : range(0,20,2)){
//            println(i+10);
//        }
//        print("\n\n");
//        for (int i : range(1,20,2)){
//            println(i);
//        }
//        print("\n\n");
//        Object[][] ans = randsample(names,5,3);
//        for (Object[] i : ans){
//            println("********items*********");
//            for (Object j :i){
//                println(j);
//            }
//        }


//        int LEN = 100;
//        Integer[] unsorted = new Integer[LEN];
//        for (int i = 0; i<LEN; i++)
//            unsorted[i] = (int) (Math.random() * 1000) + 1;
//        System.out.println("Unsorted array:");
//
//        String[] unsortedr = {"bernard", "azumah", "chris", "noah", "joshua", "benson", "kwame", "bless", "rex", "jacob"};
//        List<Object> x = new ArrayList<Object>();
//        List<Object> y = new ArrayList<Object>();
//        Map<Object, Object> z = new HashMap<Object, Object>();
//
//        for (int i : range(10)){
//            y.put(i);
//            z.put(i,"number "+i);
//        }
//
//        println(len(unsorted));
//        println(len(unsortedr));
//        println(len(x));
//        println(len(y));
//        println(len(y));
//        println(len("   "));
//        println(len("Bernard Azumah"));
//        println(len(" Bernard Azumah"));
//        println(len(" Bernard Azumah "));
//        println(len(1,2,3,4,5));
//        println(len(1.343,2232,3.000,4.23232323,5.2));
//        println(len("bernard", "names", 1,2, 4,12.45, "\n"));
//        println(len(unsorted,unsortedr));
//        println(len(unsorted,unsortedr,unsortedr));
//        Object[] n = {unsorted,unsortedr, unsorted,1,3,4};
//        Object[] m = {1};
//        println(len(n), len(m));
//
//        println(toInteger(power(2,3))+15);
//        println(getHCF(10,5));
//        println(getHCF(2,10));
//        println(getHCF(2.2,2.4));
//        println(getHCF(4.5,9));
//        println(getHCF(6,5));
//        println("hcf = "+getHCF(4,6,4,8,10));
//        println("hcf = "+getHCF(45345345,34534545,5345435,34585,34510));
//        println("*****************************");
//        println(getLCM(10,5));
//        println(getLCM(3,2));
//        println(getLCM(2.2,2.4));
//        println(getLCM(4.5,9));
//        println(getLCM(6,5));
//        println("lcm = "+getLCM(4,6,4,8,10));
//        println("lcm = "+getLCM(2,3,9,5,6,20));
//        println("*****************************");
//        println(range(2,10));
//        println("*****************************");
//        println(toInteger(getFIB(1,2, 500)));


//        printerln.setConsoleLength(60);
//        printerln.alignRight().print("Jubilee Academy School,\nPost Office Box 567,\nAccra-Ashaiman.\n20th January, 2019.");
//        printerln.alignLeft().print("Google Computers,\nPost Box 666,\nGhana-Accra.");
//        printerln.print("Dear Sir,");
//        printerln.alignCenter().print("Letter To Say My Mind.");
//        printerln.print("*".repeat(23));
//        printerln.alignLeft().print("\n\tMy name is you alrady know..This is my most awesome class..\nI think you should buy it.Ill be fun for me and you");
//        printerln.print("Very soon tho cause i might be late..Text me when you can.");
//        printerln.alignRight().print("\n\nYours Awesome,\n.............\nOtc Chingy.");
//        printerln.resetAlignment();


//        String[] names = {"bernard", "azumah", "chris", "noah", "joshua", "benson", "kwame", "bless", "rex", "jacob"};
//        Integer[] nums = {12,4,2,65,3,56,99,345,5,3,5,2,45,5,46,5};
//
//        print(names);
//        print("\n***************\n");
//        print(nums);
//        print("\nmy name is bernard ", "who are you", 1 , 567, 1.908, "i am me"+56, nums, "\n");
//
//        printerln.read("My name is ").sep("\n");
//        println(names);
//        printerln.read("").sep(" ");
//        println("***************");
//        println(nums);
//        println("mu name is bernard ", "who are you ", 1 , 567, 1.908, "i am me "+56, nums);
//        println(range(1,10));


//       Myvar v = new Myvar();
//       v.loop(60)
//               .andprint("*")
//               .endl().setConsoleLength(60)
//               .print(alignCenter("Welcome to Can You Guess It!!!", 60)).endl()
//               .print(alignCenter("To Quit Enter 'q'", 60)).endl();
//
//       v.loop(60).
//               andprint("*")
//               .endl(2)
//               .dothis(new Command() {
//           @Override
//           public void function() {
//                while (true){
//                    game();
//                    v.println(alignCenter("Do you wish to quit ? (y,n) : ",60));
//                    if (inputStr("").equalsIgnoreCase("y")){
//                        break;
//                    }
//                }
//           }
//
//           public  void game(){
//               v.randint(10);boolean guessed = false;
//               v.println(alignCenter("Magic number is set ...", 60))
//                       .println(alignCenter("Guess the number ***** : ", 60));
//               v.assign("ans", inputInt(""));
//               if (v.getVar("ans") == v.toInteger()){
//                   guessed = true;
//               }
//               while (v.getVar("ans") != v.toInteger()){
//                   v.println(alignCenter("Aww sorry, That wasnt it ...Try Again.",60));
//                   String ans = inputStr(alignCenter("\nWhats your lucky number : ",60));
//                   if (ans.equalsIgnoreCase("q")){
//                       guessed = false;break;
//                   }
//                   v.assign("ans", toInteger(ans));
//                   if (v.getVar("ans") == v.toInteger()){
//                       guessed = true;
//                   }
//               }
//               if (!guessed){
//                   v.println(alignCenter(format("You didnt guess it, the number was <>", v.toInteger()),60));
//               }
//               else{
//                   v.println(alignCenter("Yaayyyyy.... You guessed ryt",60));
//               }
//           }
//
//
//       });


//
//        //Timer, StopWatch are Runnable objects
//        Thread time = new Time();
//        Thread t = new Timing();
//        Thread s = new StopWatch();
//        Thread r = new Thread(new runner());
//
//        r.read();
//        time.read();
//        r.interrupt();
//        while (true) {
//            String ans = inputStr("");
//            if (ans.equalsIgnoreCase("q")) {
//                break;
//            }
//            else if (ans.equalsIgnoreCase("s")) {
//                ((Time) time).showTIMEoff();
//                ((StopWatch) s).setCountDownTime(inputInt("\nSet CountDown Hours  \t::\t"),
//                                                inputInt("\nSet CountDown Minutes\t::\t"),
//                                                inputInt("\nSet CountDown Seconds\t::\t"));
//                if (s.isAlive()){ s.read(); }else{ s.run(); }
//            }
//            else if (ans.equalsIgnoreCase("t")) {
//                ((Time) time).showTIMEoff();
//                if (t.isAlive()){ t.read(); }else{ t.run(); }
//            }
//            else if (ans.equalsIgnoreCase("ss")) {
//                ((StopWatch) s).stopIt();println(s.isAlive(),s.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("st")) {
//                ((Timing) t).stopIt();println(t.isAlive(),t.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("ps")) {
//                ((StopWatch) s).pause();println(s.isAlive(),s.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("pt")) {
//                ((Timing) t).pause();println(t.isAlive(),t.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("rs")) {
//                ((StopWatch) s).resumeIt();println(s.isAlive(),s.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("rt")) {
//                ((Timing) t).resumeit();println(t.isAlive(),t.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("sss")) {
//                ((StopWatch) s).reset();println(s.isAlive(),s.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("sst")) {
//                ((Timing) t).reset();println(t.isAlive(),t.isInterrupted());
//            }
//            else if (ans.equalsIgnoreCase("w")) {
//                ((Time) time).showTIMEon();
//            }
//            else if (ans.equalsIgnoreCase("o")) {
//                ((Time) time).showTIMEoff();
//            }
//            else {
//                ans = inputStr("\nInvalid option :: use q, s, w, t ,o, ss, or st \n");
//            }
//        }
//        ((Time) time).pauseIt();


//        Thread timer = new Timing();


//        Integer[] a  = {56,17,5,13,57,10,11,16,18,20,10,2,5};
//        QuickSort.printArray(swapSort(a));
//        println("\n\n");


//        MasterList<Integer> n = new MasterList<>();
//
//        Integer[] a = range(10);
//
//        MasterList<Integer> m = new MasterList();
//        m.append(5);m.append(5);
//        List<Integer> o = new ArrayList();
//        o.put(67);o.put(36);
//
//
//        n.assign(m);println(n);
//        n.assign(o);println(n);
//        n.assign(a);println(n);
//
//        for(Integer i : n) {
//            print(i);
//        }
//
//        println(n.append(35));
//        println(n.append(34));
//        println(n.appendleft(20));
//        println(n.appendleft(21));
//        println(n.pop(3));
//        println(n.popfirst());
//        println(n.poplast());
//        println(n.appendleft(20));
//        println(n.appendleft(21));
//        println(n.update(9,8));
//        println(n.updateAll(8,10));
//
//        println(n);
//
//        println(o); println(m);
//        println(n.join(m));//used to put (append) lists object or Mylist object to content of Mylist
//        println(n.join(o));
//
//        println(n);
//
//        println(n.isEmpty());
//        println(n.issubsetOf(o));
//        println(n.issupersetOf(o));
//        println(n.issubsetOf(m));
//        println(n.issupersetOf(m));
//        println(m.issubsetOf(n));
//        println(m.issupersetOf(n));
//        println(n.contains(36));
//        println(n.contains(63,4));
//        println(n.contains(5,4));
//        println(n, m);
//        println(n.contains(m));
//        println(n.contains(o));
//
//
//        println(n.sample(5));
//        println(n.sample(1,10));
//        println(n.randsample(10));
//
//        println(n);//n doesnt change because the sample functions return new objects
//        println(n.remove(10));
//        println(n.remove(10));
//        println(n.append(5,5,5,5));
//        println(n.removeAll(5));
//        println(n.insert(5,5));
//
//        println(n.hashCode());
//        println(n.indexlastOf(5));
//        println(n.indexfirstOf(5));
//        println(n.indexlastOf(50));
//        println(n.indexfirstOf(-1));
//        println(n.count(5));
//        println(n.equal(m));
//        println(n);
//        println(n.reverse());
//        println(n.toSet());
//        n = n.toSet();
//        println(n);
//        m = n.clone().update(0 ,100);
//        m.append(14);
//        println(m);
//
//        MasterList<Integer> y = new MasterList<>(randrange(10));
//
//        println(n.asSet());
//        println(n.count(5));
//        println(n.count(3));
//        y.append(5,5,5,5,5,5,5,34,3,3,5,6,65,5,365,6,5,6,54,6,4,5,34,3);
//        println(y.sort());
//        n.clear();println(n);


//
//        StatsArray n = new StatsArray(1,2,3,3,3,4,4,5,1,4,6,67,45,3,6);//randrange(100,50);
//
//
//        println(toInteger(n.toArray()));
//        println(toInteger(n.getSortedArray()));
//        println("sum ",n.getSum());
//        println("mean ",toInteger(n.getMean()));
//        println("median ",n.getMedian());
//        println("mode ",n.getMode());
//        println("mc ",n.getModeCount());
//        println("min ",n.getMin());
//        println("max ",n.getMax());
//        println("sd ",toFloat(n.getStandardDiv()));
//        println("v ",toFloat(n.getVariance()));
//
//
//        print();


//        StackForPostFitExpression stack = new StackForPostFitExpression();
//
//        stack.push("a");
//        stack.push("b");
//        stack.push("+");
//        stack.push("c");
//        stack.push("d");
//        stack.push("+");
//        stack.push("*");
//        stack.push("f");
//        stack.push("^");
//
//        while (!stack.isEmpty()){
//            print(stack.pop());
//        }

//        LinkedList<Integer> n = new LinkedList<>();
//        n.append(1);
//        n.append(2);
//        n.append(3);
//        n.append(4);
//        n.append(5);
//
//        println(n);
//        println(n.size());
//        println(n.get(4));
//        println(n.get(0));
//        println(n.isEmpty());
//        n.insert(2, 6);
//        println(n, "\t n size = "+n.size());
//        n.insert(1, 9);
//        println(n, "\t n size = "+n.size());
//        n.insert(4, 8);
//        println(n, "\t n size = "+n.size());
//        n.insert(0, -1);
//        println(n, "\t n size = "+n.size());
//        n.insert(8, 10);
//        println(n, "\t n size = "+n.size());
//        n.remove(0);
//        println(n, "\t n size = "+n.size());
//        println(n.findFirstIndexOf(1));
//        println(n.findFirstIndexOf(5));
//        n.append(5);
//        n.append(23);
//        n.append(2);
//        n.append(41);
//        println(n);
//        println(n.findLastIndexOf(5));
//        println(n.findLastIndexOf(23));
//        println(n.findLastIndexOf(65));
//        println(n.findLastIndexOf(4));
//        n.remove(n.findLastIndexOf(5));
//        println(n);
//        n.removeObject(1);
//        println(n);
//        n.append(5);n.append(5);n.append(5);n.append(5);println(n);println(n.count(5));
//        println(n.get(n.findLastIndexOf(5)));println(n.size());
//        n.replace(14,10);
//        println(n.size());
//        println(n);
//        n.replaceAll(5,13);
//        println(n);
//        n.removeAllOf(13);
//        println(n);
//        println(n.size());
//        n.replaceFirst(10, 20);
//        n.replaceLast(2,200);
//        println(n);println(n.count(10));println(n.count(100));n.clear();println(n);println(n.size());

//
//        Queue<Integer> queue = new Queue<>();
//        queue.enqueue(1);
//        queue.enqueue(2);
//        queue.enqueue(5);
//        println(queue);
//        println(queue.dequeue());
//        println(queue);
//        println(queue.size());
//
//
//        Deque<Integer> deque = new Deque<>();
//        deque.enqueueEnd(2);
//        deque.enqueueEnd(8);
//        deque.enqueueEnd(9);
//        deque.enqueueStart(0);
//        println(deque);
//        println(deque.dequeueStart());
//        println(deque);
//        println(deque.dequeueEnd());
//        println(deque);
//        print(deque.size());

//        //fix mergesort
//        Integer[] n = new Integer[]{2 ,26 ,41 ,7 ,15 ,46 ,14 ,44 ,40 ,20};
//
//        MergeSort mergeSort = new MergeSort(n);
//        println(mergeSort.getSORTED());


//        FileExplorer fm = new FileExplorer(System.getenv("USERPROFILE")+"/Desktop");

//        fm.createNewFile("data.txt")
//                .openReadWritableFile("data.txt")
//                .write("the quick brown fox")
//                .writeAppend("jumped over the lazy dog")
//                .writeAppend("line is not always what is seems", "take what you want and never look back");

//        println(fm.exploreFile("data.txt").getCurrentPath());
//        println(fm.goBack().getCurrentPath());
//        fm.exploreFile("Testing").createNewFolder("SmallTest").exploreFile("SmallTest");
//        println(fm.goBack().goBack().getCurrentPath());


//        println(fm.goForward().getCurrentPath());

//        println(fm.createNewFile("data.txt").openReadWritableFile("data.txt").getAbsoluteFilePath());
//                .write("The Quick Brown Fox Jumped Over The Lazy Dog\nsome other line of code please");

//        println(fm.openReadWritableFile("data.txt").read());
//        println();
//        println(fm.openReadWritableFile("data.txt").writeAppend("\n this is some more lines").read());
//        println();
//        println(fm.getCurrentPath());
//        fm.renameFile("data.txt", "newData.txt");
//        println(fm.openReadWritableFile("newData.txt").read());
//        println(fm.openReadWritableFile("newData.txt").readLineAt(1));
//        println(fm.getCurrentPath());
//
// try {
//            ReadWritableFile file = new ReadWritableFile(System.getenv("USERPROFILE")+"/Desktop/data.txt");
//
//            if (file.isReadWritable()) {
//                println(file);
//                println(file.write("sdfsdfsdfsdfsdfsdfsdf").read());
//                println(file.writeAppend("fhgfhgfhfhgfhg").read());
//                println(file.writeAppend("kill me now").read());
//                println(file.writeAppend("kill me now","bruh kwesi",9078978).read());println(file.numberOfLines());
//                println(file.write("safsdfsdg\nfgdfhnd","sdfsdfsdgdfgdfger","efrqwrwerwtwrgr","frwerwerwerwerwer").read());
//                println(file.readEnumerate());
//                println(file.writeReplaceLine(2, "this is it").read());
//                println(file.readLineAt(2));
//                file.clearFile();println(file.readEnumerate());println(file.numberOfLines());
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        DirectoryManager.FileExplorer fx = new DirectoryManager.FileExplorer("test");
//        fx.ls();
//
//        File test = fx.getFile("_copy.txt");
//        File dest = fx.getCurrentFile();
//        DirectoryManager.FileManager.copy(test, dest);


//        Funcs.CountDownTimer timer = new Funcs.CountDownTimer(5000, 1000) {
//            @Override
//            public void onStart() {
//                println("timer started");
//            }
//
//            @Override
//            public void onTick() {
//                println("timer tick");
//                println("time passed = "+ getTimeElapsed());
//            }
//
//            @Override
//            public void onStop() {
//                println("timer ended");
//                println("time passed = "+ getTimeElapsed());
//            }
//        };
//
//        try {
//            timer.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public static class StackForPostFitExpression extends Stack {

        public String pop() {
            String top = super.list.popfirst().toString();
            super.list.remove(0);

            if (top.equals("+") || top.equals("-") || top.equals("*") || top.equals("/") || top.equals("//") || top.equals("%") || top.equals("^")) {
                return pop() + top + pop();
            } else {
                return top;
            }
        }

    }

}



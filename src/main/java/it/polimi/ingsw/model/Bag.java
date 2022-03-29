package it.polimi.ingsw.model;

public class Bag {
    private int count; //number of students in the bag
    private Student[] bag; //array of students

    public Bag(){
        //ARRAY FOR EFFICIENCY
        this.count = 120; //10 students of each color are already initialized on the board (thus 120 in the bag at the start)
        this.bag = new Student[130]; //allocate memory for array of 130 students

        //fill bag with 130 students: 24 of each tower color
        for(int i = 0; i < 120; i++){
            //for each color, add 24 students to bag
            for (PawnColor color : PawnColor.values()){
                for (int j = 0; j < 24; j++){
                    bag[i] = new Student(color);
                }
            }
        } // bag is filled with 120 students, 24 of each color
    }
    public Bag(int num_students){
        //ARRAY FOR EFFICIENCY
        this.count = num_students; //10 students of each color are already initialized on the board (thus 120 in the bag at the start)
        this.bag = new Student[130]; //allocate memory for array of 130 students

        //fill bag with num_students amount of students: same count for each tower color
        for(int i = 0; i < num_students; i++){
            //for each color, add (num_students/5) students to bag
            for (PawnColor color : PawnColor.values()){
                for (int j = 0; j < (num_students / 5); j++){
                    bag[i] = new Student(color);
                }
            }
        } // bag is filled evenly with num_students of the 5 colors
    }

    //return number of students in bag
    public int getCount(){
        return this.count;
    }

    //return and extract random student
    public Student getRandomStudent(){
        int randomIndex = (int)(this.count * Math.random()); //select random int between 0 - (count-1)
        Student studentOut = bag[randomIndex]; //write random student to studentOut
        bag[randomIndex] = bag[this.count - 1]; //take last element in array and move to spot of randomly extracted student
        bag[this.count - 1] = null; //empty last item in array
        this.count--; //decrement count
        return studentOut;
    }

    //return InitBag with 10 students
    public Bag getInitBag(){
        return new Bag(10);
    }
}
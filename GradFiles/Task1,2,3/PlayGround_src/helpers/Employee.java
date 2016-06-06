package helpers;

public class Employee
   {
       private String name;
       private double salary;
       private int id;

       public Employee()
       {
           name = "";
           salary = 0.0;
           id = 0;
       }

       public Employee(String n, double s, int i)
       {
           name = n;
           salary = s;
           id = i;      
       }

       public void print()
       {
           System.out.println(name + " " + id + " " + salary);
       }
   }
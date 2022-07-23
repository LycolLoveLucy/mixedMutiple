package com.jielu.web.request;

public class FeignGetRequest {

    private  String firstName;

    private  String secondName;

    private  Expand expand;

    public Expand getExpand() {
        return expand;
    }

    @Override
    public String toString() {
        return "FeignGetRequest{" +
                "firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", expand=" + expand.toString() +
                '}';
    }

    public void setExpand(Expand expand) {
        this.expand = expand;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

   public static  final  class Expand{

        private  int age;

        private  String leveTwo;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getLeveTwo() {
            return leveTwo;
        }

        public void setLeveTwo(String leveTwo) {
            this.leveTwo = leveTwo;
        }

        @Override
        public String toString() {
            return "Expand{" +
                    "age=" + age +
                    ", leveTwo='" + leveTwo + '\'' +
                    '}';
        }
    }
}

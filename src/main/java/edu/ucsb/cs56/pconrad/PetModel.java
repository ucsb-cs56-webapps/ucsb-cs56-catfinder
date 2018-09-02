package edu.ucsb.cs56.pconrad;


/*
 Will represent a cat. Of course it can probably be used for most other pets.
 We will initialize the values because we may not be able to get some of them.
 Note that Freemark requires getters to make the lists work.
  */
public class PetModel {
    public String name = "";
    public String gender = "";
    public String description = "";
    public String img = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Black_Cat_Vector.svg/2000px-Black_Cat_Vector.svg.png";

    public String getName(){
        return name;
    }

    public String getGender(){
        return gender;
    }

    public String getDescription(){
        return description;
    }

    public String getImg(){
        return img;
    }
}

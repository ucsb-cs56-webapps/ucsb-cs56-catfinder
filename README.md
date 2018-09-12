# ucsb-cs56-catfinder
Check out the running product here: https://ucsb-cs56-wilson-08-18.herokuapp.com/

To get it working on your own repo, create a .env file with these values in it:

```
PETFINDER_KEY={YOUR PETFINDER API KEY}
```

A Petfinder key can be requested here: https://www.petfinder.com/developers/api-key

* [javadocs](https://ucsb-cs56-pconrad.github.io/sparkjava-01/apidocs/index.html)
* [web page generated by Maven (NOT THE WEB APP)](https://ucsb-cs56-pconrad.github.io/sparkjava-01/index.html).  This is simply documentation automatically generated by Maven, not the actual running web app.

# Basics of JSON

Let's start with some simple questions.

1. What does it stand for?  
JSON - JavaScript Object Notation.

2. What is it?  
A data interchange format that gathers and shares data among applications and interfaces.

3. Why JSON?  
It is a text-based, language-independent data exchange format that is easy for human to read.

4. What does it look like?  
JSON provides data to calling functions in key-value pairs, where key is the name of the variable and value is its corresponding value. 

5. How are the key value pairs stored?  
There are two structured types in JSON, objects and arrays. An object is unordered and has zero or more key-value pairs, whereas an array is ordered and has zero or more values. The values can be strings, numbers, booleans, null, or even these two structured types.

6. What is a RestAPI?  
A RESTful API is an application program interface (API) that uses HTTP requests to GET, PUT, POST and DELETE data. Basically when users visit a website, they receive and send JSON files using the RestAPI to allow connections and interactions with cloud services. 

Now, we should get into more details. To process JSON files, we will need JSON.simple, which provides reading and writing to JSON streams. Notice that it cannot be replaced with org.json as the latter library doesn't offer streaming support.

After importing the necessary packages with the line below,
```
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
```
we will proceed to write a simple JSON file with Java and create a list for the Class of 2020 as an example. The first thing to do is to create a general JSONObject.
```
JSONObject class_of_2020 = new JSONObject();
```
We can add a student to class_of_2020 by creating another JSONObject and put in a few pieces of information.
```
JSONObject yang = new JSONObject();
yang.put("first_name", "Gaucho");
yang.put("last_name", "Yang");
yang.put("age", 20);
```
In addition to simple key-value pairs, we can also add an array of classes taken and their corresponding grades. Because we need to put two key-value pairs for each course, we might just create a LinkedHashMap instead. Be careful that JSONArray uses add, whereas JSONObject uses put.
```
JSONArray courses_taken = new JSONArray();
m = new LinkedHashMap(2);
m.put("name", "cs56");
m.put("grade", "A");
courses_taken.add(m);
m = new LinkedHashMap(2);
m.put("name", "cs48");
m.put("grade", "A-");
courses_taken.add(m);
yang.put("courses", courses_taken);
class_of_2020.put("Gaucho Yang", yang);
```
Finally, we save it into a JSON file.
```
PrintWriter pw = new PrintWriter("ClassOf2020.json");
pw.write(class_of_2020.toJSONString());
pw.flush();
pw.close();
```
The results are shown below. Notice that since JSONObject is unordered, the output shown here doesn't perserve the order we input.
```
{
    "Gaucho Yang":{
        "age":"20",
        "first_name":"Gaucho",
	"courses":[
		 {
	 	"name":"cs56", "grade":"A"
	 	},
		 {
		 "name":"cs48", "grade":"A-"
	 	}
	 ],
        "last_name":"Yang"
    }
}
```
Now, we will look into reading from a JSON file. In addition to the two packages we imported before, a third one is required.
```
import org.json.simple.parser.*;
```
We create a JSONParser with the name of the desired file and cast the result to a JSONObject. 
```
Object obj = new JSONParser().parse(new FileReader("ClassOf2020.json"));
JSONObject class_of_2020 = (JSONObject) obj;
```

For simple key-value pairs such as first_name, we can simply use the get function and cast the output.
```
JSONObject yang = (JSONObject) class_of_2020.get("Gaucho Yang");
String firstName = (String) yang.get("first_name");
```
For the list of courses, we will need one iterator for the JSONArray and a second iterator for the LinkedHashMap.
```
JSONArray courses = (JSONArray) yang.get("courses");
Iterator itr1 = courses.iterator();
while (itr1.hasNext()){
	iterator<Map.Entry> itr2 = ((Map) itr1.next()).entrySet().iterator();
        while (itr1.hasNext()) {
             Map.Entry pair = itr2.next();
             System.out.println(pair.getKey() + " : " + pair.getValue());
        }
}         
```
# Using a RestAPI to retrieve JSON and load into our model

We base our app on the API provided by PetFinder, which is also a RESTful API. It will be used to retrieve a JSON containing a list of cats. 
Documentation for this API can be found here: https://www.petfinder.com/developers/api-docs.

To read data, we will use GET calls, which has the following format:
```
http://api.petfinder.com/my.method?key=12345&arg1=foo
```
In our case, we need to use pet.find, where the required parameters are "key" and "location". We want our response to be in JSON, so we will also pass that argument and our query will look like the following:

```
http://api.petfinder.com/pet.find?key=" + key + "&animal=cat&location=93117&format=json
```

Here is the JSON tree returned from the API. A lot of irrelevant fields are removed for simplicity.
However, the paths to the data we want are the same in the edited version and the actual. I would recommend using a tool such as Insomnia to see what our query will return.

```
{
	"petfinder": {
		"pets": {
			"pet": [
				{
					"media": {
						"photos": {
							"photo": [
								{
									"@size": "pnt",
									"$t": "http://photos.petfinder.com/photos/pets/35958838/1/?bust=1493239347&width=60&-pnt.jpg",
									"@id": "1"
								}
							]
						}
					},
					
					"name": {
						"$t": "Elsie"
					},
					"sex": {
						"$t": "F"
					},
					"description": {
						"$t": "Elsie came to us in desperate need of a home.  A senior cat at 14 years old, she is in good health considering the miles she's traveled.  Elsie is very sweet and petite.  We're hoping there will be an angel out there looking for an older companion.  Elsie weighs 7lbs.and is microchipped."
					}
				},
				{
					"media": {
						"photos": {
							"photo": [
								{
									"@size": "pnt",
									"$t": "http://photos.petfinder.com/photos/pets/40943615/1/?bust=1518559770&width=60&-pnt.jpg",
									"@id": "1"
								}
							]
						}
					},
					"name": {
						"$t": "Gypsy"
					},
					"sex": {
						"$t": "F"
					},
					"description": {
						"$t": "Our adoptable cats are spayed/neutered, vaccinated, and microchipped.\n"
					}
				}
			]
		}
	}
}
```

Our model representing each cat will be defined in PetModel.java. There will be default values in case some data are missing from the API.

```
public class PetModel {
    public String name = "";
    public String gender = "";
    public String description = "";
    public String img = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Black_Cat_Vector.svg/2000px-Black_Cat_Vector.svg.png";
}
```

To start, add this JSON dependency. It will allow us to create JSONObjects.
```
      <!-- JSON -->
      <dependency>
          <groupId>org.json</groupId>
          <artifactId>json</artifactId>
          <version>20150729</version>
      </dependency>
```
In our main, we can use an Arraylist to store all of our created PetModels.
```
        ArrayList<PetModel> pets = new ArrayList<>();

```

So let's really start! In this short guide, everything will be done within here:

```
get("/sample", (req, res) -> { we will do everything in here }

```
For details on config var values, refer to:
https://devcenter.heroku.com/articles/config-vars.
Store your key in an .env file and be sure to ignore that from git.
If you don't want to deal with that yet, just replace 'key' with your API key. (bad practice makes perfect)
```
            String key = System.getenv("PETFINDER_KEY");
            String urlString = "http://api.petfinder.com/pet.find?key=" + key + "&animal=cat&location=93117&format=json";
```
Create a URL with the url formed and open a connection. We will be 'getting', so set the request method like so.
Get the response code. If it is not 200, then that means there is an issue. Note if your URL is https, then
you should use HttpsURLConnection and call setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
on it.

```
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.connect();

            int responsecode = connection.getResponseCode();
            if (responsecode != 200) { System.out.println("oh no");}
```
Now that we are connected, we want to read in the data provided by the API. Here we will do this with a Scanner.
Build a String with the data. We can use this string to form our JSONObject. If we simply do System.out.println(inline), we will get the tree shown earlier.

```
            Scanner sc = new Scanner(url.openStream());
            String inline = "";
            while (sc.hasNext()) {
                inline += sc.nextLine();
            }
            sc.close();
```

We will then store every pet in the JSON file as a PetModel by following the steps below:

1. Create a new JSONObject from the string read by the scanner.  
2. For each JSONObject, "<JSONObject>".get("<field>") can be called to retrieve the corresponding value. In our case, make each pet in the array of pets into a JSONObject and cast each field in pet to a JSONObject. Notice that some fields in the JSON tree have brackets instead of curly brackets, which means they need to be casted to a JSONArray instead of a JSONObject.  
3. After getting to the innermost layer, we call .toString() on the JSON Object to return the string of its values.

Watch out for fields that can be empty. Trying to create an object from it will cause an error, which can be avoided by checking if the length of the object is 0.

The idea here is to iterate through the JSON array of pets. Parse the data from each pet, store it in a 
PetModel, and then add it to an ArrayList of PetModels.
```
            JSONObject json = new JSONObject(inline);
            json = (JSONObject) json.get("petfinder");
            json = (JSONObject) json.get("pets");

            JSONArray json_pets = (JSONArray) json.get("pet");
            for (int i = 0; i < json_pets.length(); i++) {
                PetModel pet = new PetModel();
                JSONObject json_pet = (JSONObject) json_pets.get(i);
                JSONObject json_pet_name = (JSONObject) json_pet.get("name");
                JSONObject json_pet_gender = (JSONObject) json_pet.get("sex");
                JSONObject json_pet_description = (JSONObject) json_pet.get("description");
                JSONObject json_pet_img = (JSONObject) json_pet.get("media");

                if (json_pet_img.length() != 0) {
                    json_pet_img = (JSONObject) json_pet_img.get("photos");
                    JSONArray json_pet_img_arr = (JSONArray) json_pet_img.get("photo");

                    if (json_pet_img_arr.length() != 0) {
                        for (int k = 0; k < json_pet_img_arr.length(); k++) {
                            json_pet_img = (JSONObject) json_pet_img_arr.get(0);
                        }
                    }

                    pet.img = json_pet_img.get("$t").toString();
                }

                if (json_pet_name.length() != 0) {
                    pet.name = json_pet_name.get("$t").toString();
                }
                if (json_pet_description.length() != 0) {
                    pet.description = json_pet_description.get("$t").toString();
                }
                if (json_pet_gender.length() != 0) {
                    pet.gender = json_pet_gender.get("$t").toString();
                }

                pets.add(pet);
            }
```

We can iterate through our list of PetModels to check if things are working.
```
            for ( PetModel cat:pets) {
                System.out.println("name: " + cat.name + " gender: " + cat.gender);
            }

            return "";
```


# Using Freemarker as a template engine

With a template engine, a template can be thought of as a 'view' in the Model-View-Controller design pattern. It is essentially a layout with parameters. Here is how to start using FreeMarker.

In your pom.xml, include Freemarker as a dependency:

```
        <dependency>
          <groupId>org.freemarker</groupId>
          <artifactId>freemarker</artifactId>
          <version>2.3.28</version>
        </dependency>
       
```

Create a layout file in your jar. Freemarker uses .ftl files that are similar to html. Values can be inserted with ${id}. For example:

```
<html>
<head>
    <title>${title_value}</title>
</head>
<body></body>
</html>
```

Now, set up a Configuration instance at the beginning of the Java file. In this example, a layouts folder is located at /src/main/resources/layouts/, where the templates will be loaded. A template named "home.ftl" is used.

```
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
    cfg.setClassForTemplateLoading(CatFinder.class, "/layouts/");
    cfg.setDefaultEncoding("UTF-8");
```

To insert values into the template:

```
get("/", (Request req, Response res) -> {
            StringWriter writer = new StringWriter();
            try {
            Map attributes = new HashMap();
            attributes.put("title_value", "This is the home page");
                Template homeTemplate = cfg.getTemplate("home.ftl");
                homeTemplate.process(attributes, writer);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("home.ftl not found!");
                Spark.halt(500);
            }
            return writer;
        });
```

Using lists is a little trickier. In your .ftl file, add something like this:

```
<#list cats as cat>
    <div>
         <img src=${cat.img} width="300" height="200">
         <h3>${cat.name}</h3>
         <div">${cat.description}</div>
    </div>
</#list>
```

We shall define a separate class called PetModel in the same folder as below.
```
public class PetModel {
    public String name = "";
    public String description = "";
    public String img = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Black_Cat_Vector.svg/2000px-Black_Cat_Vector.svg.png";

    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }

    public String getImg(){
        return img;
    }
}
```

Each member should have a getter. The variable names should correlate to the labels in your ftl.
In the attributes, the 'cats' within your template file should be mapped to an ArrayList of PetModels. Processing the template will do all the mapping of each PetModel member variable to its place in the template.

```
get("/results", (req, res) -> {

            StringWriter writer = new StringWriter();
            try {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("cats", cats);
                Template resultsTemplate = cfg.getTemplate("results.ftl");
                resultsTemplate.process(attributes, writer);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("results.ftl not found!");
                Spark.halt(500);
            }
            return writer;

        });
```



# To use

| To do this | Do this |
| -----------|-----------|
| run the program | Type `mvn exec:java`.  Visit the web page it indicates in the message |
| check that edits to the pom.xml file are valid | Type `mvn validate` |
| clean up so you can recompile everything  | Type `mvn clean` |
| edit the source code for the app | edit files in `src/main/java`.<br>Under that the directories for the package are `edu/ucsb/cs56/pconrad`  |
| edit the source code for the app | edit files in `src/test/java`.<br>Under that the directories for the package are `edu/ucsb/cs56/pconrad`  |
| compile    | Type `mvn compile` |
| run junit tests | Type `mvn test` |
| build the website, including javadoc | Type `mvn site-deploy` then look in either `target/site/apidocs/index.html`  |
| copy the website to `/docs` for publishing via github-pages | Type `mvn site-deploy` then look for javadoc in `docs/apidocs/index.html` |	
| make a jar file | Type `mvn package` and look in `target/*.jar` |

| run the main in the jar file | Type `java -jar target/sparkjava-demo-01-1.0-jar-with-dependencies.jar ` |
| change which main gets run by the jar | Edit the `<mainClass>` element in `pom.xml` |

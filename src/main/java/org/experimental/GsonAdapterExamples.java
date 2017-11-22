package org.experimental;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mattgross on 11/14/2017.
 */
public class GsonAdapterExamples {

    @Test
    public void tempTest() {
        Gson typeOnly = new GsonBuilder()
            .registerTypeAdapter(Cat.class, new CatAdapter())
            .registerTypeAdapter(Dog.class, new DogAdapter())
            .create();

        // The type hierarchy adapter can deserialize child classes to a common parent class using a single adapter.
        // If you wanted to, you could also return just an instance of the parent class with only the JSON relevant
        // to the parent class. The type hierarchy adapter is somewhat easier to initially create, at the expense
        // of a more complex serializer and deserializer. But with this, you do not need separate Cat and Dog
        // adapter classes.
        Gson typeAndHier = new GsonBuilder()
            .registerTypeHierarchyAdapter(Animal.class, new AnimalAdapter())
            .create();

        Cat theCat = new Cat("Lucy", "brown");
        Dog theDog = new Dog("Rover", "Dad");

        String catJson = typeOnly.toJson(theCat);
        String dogJson = typeOnly.toJson(theDog);

        Cat newCat = typeOnly.fromJson(catJson, Cat.class);
        Dog newDog = typeOnly.fromJson(dogJson, Dog.class);

        Assert.assertNotNull(newCat);
        Assert.assertNotNull(newDog);

        String hierCatJson = typeAndHier.toJson(theCat);
        String hierDogJson = typeAndHier.toJson(theDog);

        Cat catAnimal = typeAndHier.fromJson(hierCatJson, Cat.class);
        Animal dogAnimal = typeAndHier.fromJson(hierDogJson, Animal.class);

        Assert.assertNotNull(catAnimal);
        Assert.assertNotNull(dogAnimal);
    }

    private class AnimalAdapter implements JsonSerializer<Animal>, JsonDeserializer {

        @Override
        public Animal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            Animal theAnimal;
            JsonObject asObj = json.getAsJsonObject();
            String name = asObj.get("name").getAsString();

            if (asObj.get("color") != null) {
                theAnimal = new Cat(name, asObj.get("color").getAsString());
            } else {
                theAnimal = new Dog(name, asObj.get("owner").getAsString());
            }

            return theAnimal;
        }

        @Override
        public JsonElement serialize(Animal src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", src.name);

            if (src instanceof Cat) {
                json.addProperty("color", ((Cat) src).color);
            } else if (src instanceof Dog) {
                json.addProperty("owner", ((Dog) src).owner);
            }

            return json;
        }
    }

    private class CatAdapter implements JsonSerializer<Cat>, JsonDeserializer<Cat> {

        @Override
        public Cat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            JsonObject asObj = json.getAsJsonObject();
            return new Cat(asObj.get("name").getAsString(), asObj.get("color").getAsString());
        }

        @Override
        public JsonElement serialize(Cat src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", src.name);
            json.addProperty("color", src.color);
            return json;
        }
    }

    private class DogAdapter implements JsonSerializer<Dog>, JsonDeserializer<Dog> {

        @Override
        public Dog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            JsonObject asObj = json.getAsJsonObject();
            return new Dog(asObj.get("name").getAsString(), asObj.get("owner").getAsString());
        }

        @Override
        public JsonElement serialize(Dog src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", src.name);
            json.addProperty("owner", src.owner);
            return json;
        }
    }

    private class Animal {
        public String name;

        public Animal(String name) {
            this.name = name;
        }
    }

    private class Cat extends Animal {
        public String color;

        public Cat(String name, String color) {
            super(name);
            this.color = color;
        }
    }

    private class Dog extends Animal {
        public String owner;

        public Dog(String name, String owner) {
            super(name);
            this.owner = owner;
        }
    }
}

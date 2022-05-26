package com.example.functional;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.ls.LSOutput;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Stream;

@SpringBootApplication
public class FunctionalApplication {


	public static void main(String[] args) {
		//I stablish the connection
		MongoClient mongoClient = MongoClients.create("mongodb+srv://mdyagual:mdyagual@clusterferreteria.aum6z.mongodb.net/?retryWrites=true&w=majority");
		// I'm getting the table/collection that i want to use
		MongoCollection<Document> collection = mongoClient.getDatabase("sample_restaurants").getCollection("restaurants");
		//I'm saving all the data from the previous line into an ArrayList of Document
		ArrayList<Document> dataRestaurants = collection.find().into(new ArrayList<>());

		System.out.println("\n\n------OUTPUT------------\n\n");
		// Use this line to remember how the data looks like
		dataRestaurants.stream().map(Document::toJson).limit(5).forEach(System.out::println);

		//1: Get the boroughs that start with letter 'B'

		//First we define the condition to use on filter

		/*
		Predicate<Document> boroughWithB = (restaurant -> restaurant.get("borough").toString().startsWith("B"));
		//Second we define a function that will received the 'database' and will return a Stream of documents
		Function<ArrayList<Document>, Stream<Document>> getBoroughs = (dbRest) -> dbRest.stream()
				.filter(r -> boroughWithB.test(r));
		//Finally show the result
		Consumer<Stream<Document>> resultF1 = (value) -> value.forEach(r -> System.out.println(r.get("name")+": "+r.get("borough")));
		System.out.println("Filter #1");

		resultF1.accept(getBoroughs.apply(dataRestaurants));
		*/


		//2: Get restaurants that have as cuisine 'American'

		//First we define the condition to use on filter

		/*

		Predicate<Document> isAmerica = (data) -> data.get("cuisine").toString().equals("American");
		//Second we define a function that will received the 'database' and will return a Stream of documents
		Function<ArrayList<Document>,Stream<Document>> cuisineAmerican = (dbRest) -> dbRest.stream()
				.filter(r -> isAmerica.test(r));
		//Finally show the result
		Consumer<Stream<Document>> resultF2 = (value) -> value.forEach(r -> System.out.println( r.get("name")+"------------"+r.get("cuisine")));
		System.out.println("Filter #2");
		resultF2.accept(cuisineAmerican.apply(dataRestaurants));


		 */

		//TO DO
		/*3: Get the amount of restaurants whose name is just one word
		Keep it in mind that a restaurant e.g McDonals have some locals in different directions and also some records hasn't names assigned
		HINT: Remember that if the restaurant's name has spaces that means it has more than 1 word*/

		System.out.println("\n");

		Predicate<String> isNotBlank = (name) -> !name.isBlank();
		Predicate<String> hasOneWord = (name) -> name.trim().split(" ").length == 1;
		Predicate<Document> isCountable = (data) -> isNotBlank.test(data.get("name").toString()) && hasOneWord.test(data.get("name").toString());

		Function<ArrayList<Document>,Stream<Document>> restaurantsOneWord = (dbRest) -> dbRest.stream()
				.filter(r -> isCountable.test(r)).distinct();

		Consumer<Stream<Document>> resultF3 = (value) -> System.out.println("Number of restaurants with one word: " + value.count());
		System.out.println("Filter #3");
		resultF3.accept(restaurantsOneWord.apply(dataRestaurants));

		System.out.println("\n");

		/*4: Get all the restaurants that received grade C in the most recent data
		HINT: The recent score is always the first one inside the list of the key "grades".*/


		//check if is in C category
		Predicate<ArrayList<Document>> checkC = (value) -> value.stream().findFirst().get().get("grade").equals("C");
		//check if exists
		Predicate<ArrayList<Document>> checkExists = (value) -> value.stream().findFirst().isPresent();

		//check if exists and in B category
		Predicate<ArrayList<Document>> receivedCDocument = (data) -> checkExists.test(data) && checkC.test(data);

		Function<ArrayList<Document>, Stream<Document>> restaurantsWithCGrade = (dbRest) -> dbRest.stream()
				.filter(r -> receivedCDocument.test((ArrayList<Document>) r.get("grades")));

		Consumer<Stream<Document>> resultF4 = (value) -> value.forEach(r -> System.out.println( r.get("name")+" is a restaurant with a recent C grade"));

		System.out.println("Filter #4");
		resultF4.accept(restaurantsWithCGrade.apply(dataRestaurants));

		System.out.println("\n");


		/*5: Sort all the restaurants by the grade that has received in the most recent date. If the are not receiving a grade yet (grade=Not Yet Graded), ignore them.
		* HINT: Consider create a small Restaurant object with the data that you need to archive this exercise*/

		//get grade
		Function<ArrayList<Document>, String> getGrade = (value) -> value.stream().findFirst().get().get("grade").toString();

		//limiting to 5 for testing purposes
		dataRestaurants.stream()
				.filter(r -> checkExists.test((ArrayList<Document>) r.get("grades")))
				.sorted(Comparator.comparing(r -> getGrade.apply((ArrayList<Document>) r.get("grades")))).limit(5).forEach(System.out::println);


		//6: Get the restaurant with B category with the highest score



		//check if is in B category
	//	Predicate<ArrayList<Document>> checkB = (value) -> value.stream().findFirst().get().get("grade").equals("B");

		//check if in B category and exists
	/*	Predicate<ArrayList<Document>> receivedBDocument = (data) -> checkExists.test(data) && checkB.test(data);

		Function<ArrayList<Document>, Stream<Document>> restaurantWithBGradeAndHighestScore = (dbRest) -> dbRest.stream()
				.filter(r -> receivedBDocument.test((ArrayList<Document>) r.get("grades")));

		Consumer<Stream<Document>> resultF6 = (value) -> System.out.println("test: " + value.toString());

		System.out.println("Filter #6");
		resultF6.accept(restaurantWithBGradeAndHighestScore.apply(dataRestaurants));

		System.out.println("\n"); */


		/*7 (Optional): Investigate zip function (import org.springframework.data.util.StreamUtils;) to generate a list of strings with the next elements:
		-A stream that contains all the names of the restaurant
		-Another stream that contains the amount of words used on the restaurants's name
		Output expected
		xxxxx has 1 word
		xxxx yyyyy zz have 3 words
		xxxx yyyy have 2 words
		.
		.
		If you can solved it and if in the calculator activity you don't complete/implement the division operation, this will get considerer.
		*/



	}

}

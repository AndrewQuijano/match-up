package project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class project 
{
	private static String file_location = "C:\\Users\\Andrew\\Desktop\\match-up\\log.txt";
	private static String data_location = "C:\\Users\\Andrew\\Desktop\\match-up\\data.csv";
	private static int LINE_SIZE = (5 * 2) + 1;
	private static String [] filtered_chars = {"[", "]", ","};
	private static ArrayList<Double []> data = new ArrayList<Double []>();
	
	public static void main(String [] args)
	{
		create_dataset();
	}
	
	private static void create_dataset()
	{
		File file = new File(file_location);
		Scanner scr = null;
		String line;
		String [] line_parts;
		// Goal get List of both sides... (X for data Matrix)
		// For each team combat, Y = -1, 0, 1 (Loss, Tie, Win)
		ArrayList<Integer []> rows = new ArrayList<Integer []>();
		
		try
		{
			scr = new Scanner(file);
			while(scr.hasNext())
			{
				line = scr.nextLine();
				if (line.contains("Round"))
				{	
					Integer [] tuple = new Integer[LINE_SIZE];
					Double [] data_entry = new Double[4];
		
					// Next 3 Lines got data I want to have!
					// Get the first team's list! index 4, 5, 6, 7, 8 are what I want!
					line = scr.nextLine();
					line = filter_characters(line);
					line_parts = line.split(" ");
					for(int i = 0; i < 5; i++)
					{
						tuple[i] = Integer.parseInt(line_parts[4+i]);
					}
					
					// Get the second team's list!
					line = scr.nextLine();
					line = filter_characters(line);
					line_parts = line.split(" ");
					for(int i = 0; i < 5; i++)
					{
						tuple[i+5] = Integer.parseInt(line_parts[4+i]);
					}
					
					// Get the scores and apply the label -1, 0, 1
					// Scores will be in index 2 and 4. Beware of stray comma!
					line = scr.nextLine();
					line = line.replace(",", "");
					line_parts = line.split(" ");
					int team_one_score = Integer.parseInt(line_parts[2]);
					int team_two_score = Integer.parseInt(line_parts[4]);
					
					// Get Mean and Variance of team 1
					data_entry[0] = mean(tuple, 0, 5);
					data_entry[1] = variance(tuple, 0, 5);
					
					// Get Mean and Variance of team 2
					data_entry[2] = mean(tuple, 5, 10);
					data_entry[3] = variance(tuple, 5, 10);
					
					// Fill up y with score
					if(team_one_score > team_two_score)
					{
						tuple[10] = 1;
					}
					else if(team_one_score == team_two_score)
					{
						tuple[10] = 0;
					}
					else
					{
						tuple[10] = -1;
					}
					rows.add(tuple);
					data.add(data_entry);
				}
			}
			try 
			{
				print_csv(rows);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException ex) 
		{
			ex.printStackTrace();
		}	
	}
	
	
	// team 1 wins = 1, team 1 and team 2 tie, team 1 loses = -1
	private static void print_csv(ArrayList<Integer []> tuples) throws IOException
	{
		//FileOutputStream outputStream = new FileOutputStream(data_location);
		BufferedWriter writer = new BufferedWriter(new FileWriter(data_location));
		String output = "";
		writer.write("L1_1,L1_2,L1_3,L1_4,L1_5,L2_1,L2_2,L2_3,L2_4,L2_5,L1_mean,L1_variance,L2_mean,L2_variance,Win/Loss/Tie\n");
		for (int i = 0; i < tuples.size();i++)
		{
			/*
			output = Arrays.toString(tuples.get(i));
			output = output.replace(" ", "");
			output = output.replace("]", "");
			output = output.replace("[", "");
			*/
			Integer [] temp = tuples.get(i);
			Double [] data_temp = data.get(i);
			
			// Print L_1, L_2
			for (int j = 0; j < LINE_SIZE - 1;j++)
			{
				output += String.valueOf(temp[j]) + ",";
			}
			// Print means/varainces
			for (int k = 0; k < 4; k++)
			{
				output += String.valueOf(data_temp[k] + ",");
			}
			output += String.valueOf(temp[10]);
			
			writer.write(output + '\n');
			writer.flush();
			output = "";
		}
		writer.close();
	}
	
	private static String filter_characters(String input)
	{
		for(int i = 0; i < filtered_chars.length; i++)
		{
			if(input.contains(filtered_chars[i]))
			{
				input = input.replace(filtered_chars[i], "");
			}
		}
		return input;
	}

	private static Integer [] generate_random(int max, int min, int sum, int size)
	{
		// Initialized Array, set to minimum
		Integer [] generated = new Integer[size];
		Arrays.fill(generated, min);
		
		Random coin = new Random();
		int index = 0;
		
		// Flip a coin. 1 fill, 0 no fill move to next.
		while(sum(generated, 0, generated.length) != sum)
		{
			// Avoid Index out of Bounds!
			if(index == size)
			{
				index = 0;
			}
			// Skip Index if it is max!
			if(generated[index] == max)
			{
				++index;
				continue;
			}
			
			// Flip the coin [0, 1], fill/skip and continue
			int fill = coin.nextInt(2);
			if(fill == 1)
			{
				++generated[index];
			}
			++index;
		}
		System.out.println(Arrays.toString(generated));
		return generated;
	}
	
	private static int sum(Integer [] a, int min, int max)
	{
		int answer = 0;
		for (int i = min; i < max; i++)
		{
			answer += a[i];
		}
		return answer;
	}
	
	// Get Mean
	private static double mean(Integer [] a, int min, int max)
	{
		return (double) sum(a, min, max)/(max - min);
	}
	
	// Get Variance
	private static double variance(Integer [] a, int min, int max)
	{
		double mean = mean(a, min, max);
		double variance = 0;
		for (int i = min; i < max; i++)
		{
			variance += (a[i].doubleValue() -  mean)*(a[i].doubleValue() -  mean);
		}
		variance = variance/(max - min);
		return variance;
	}
}
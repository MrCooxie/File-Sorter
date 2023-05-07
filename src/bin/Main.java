package bin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);

         while (true) {
             printActions();
             int input = scanner.nextInt();
             switch (input) {
                 case 1 -> {
                     System.exit(1);
                 }
                 case 2 -> {
                     scanner.nextLine();
                     System.out.println("Enter folder to sort:");
                     String folderToSort = scanner.nextLine();
                     sortFolder(folderToSort);
                 }
                 default -> {
                     System.out.println("Wrong input");
                 }
             }
         }
    }

    private static void printActions() {
        System.out.println("1 - Close program");
        System.out.println("2 - Sort Folder");
    }

    private static void sortFolder(String source) {
        ArrayList<MyFile> myFileArrayList = new ArrayList<>();
        File file = new File(source);
        File[] listOfFiles = file.listFiles();
        for (File curFile : listOfFiles) {
            int posOfDot = curFile.getName().indexOf(".");
            if (posOfDot == -1) {
                continue;
            }
            long realLength = curFile.getName().substring(0, posOfDot).length();

            myFileArrayList.add(new MyFile(curFile.getName().substring(0, (int) realLength), realLength, curFile.getPath(), curFile.getName().substring((int) realLength)));
        }
        if (listOfFiles.length != 0) {
            divideSimilarFilesIntoFolders(myFileArrayList, source);
        }
    }

    private static void divideSimilarFilesIntoFolders(ArrayList<MyFile> files, String rootLocation) {
        ArrayList<String> names = new ArrayList<>();
        // Create an array of only names
        for (MyFile file : files) {
            names.add(file.name());
        }
        // Use HashSet to exclude duplicate values
        HashSet<String> similarities = new HashSet<>();
        //Split each element of Set on regex "_"
        for (String name : names) {
            String[] splitString = name.split("_");
            similarities.addAll(Arrays.asList(splitString));
        }
        // Convert the Set to String[]
        Object[] similaritiesObject = (similarities.stream().sorted()).toArray();
        String[] similaritiesString = new String[similaritiesObject.length];
        for (int i = 0; i < similaritiesString.length; i++) {
            similaritiesString[i] = (String) similaritiesObject[i];
        }
        // Loop through each element of the String[]
        for (String current : similaritiesString) {
            int amountOfSameFiles = 0;
            int previousFileIndex = 0;
            try {
                //Loop through every element of files to check does any of the files contain current (similar part of file name)
                for (int j = 0; j < files.size(); j++) {
                    if (files.get(j).name().contains(current + "_") || files.get(j).name().contains("_" + current)) {
                        amountOfSameFiles++;
                        //Logic for creating a folder only if 2 similar files are found
                        if (amountOfSameFiles > 1) {
                            if (amountOfSameFiles == 2) {
                                //Create the folder if 2 similar files were found
                                new File(rootLocation + "\\" + current).mkdir();
                                    File file = new File(rootLocation + "\\" + current + "\\" + files.get(previousFileIndex).name() + files.get(previousFileIndex).fileExtension());
                                    Files.copy(Path.of(files.get(previousFileIndex).path()), file.toPath());
                                    File file1 = new File(rootLocation + "\\" + current + "\\" + files.get(j).name() + files.get(j).fileExtension());
                                    Files.copy(Path.of(files.get(j).path()), file1.toPath());

                                    new File(files.get(previousFileIndex).path()).delete();
                                    new File(files.get(j).path()).delete();
                                    files.remove(previousFileIndex);
                                    files.remove(j - 1);
                                    j -= 2;

                            } else {
                                // if more than 2 files are found don't create a new folder
                                File file = new File(rootLocation + "\\" + current + "\\" + files.get(j).name() + files.get(j).fileExtension());
                                Files.copy(Path.of(files.get(j).path()), file.toPath());
                                new File(files.get(j).path()).delete();
                                files.remove(j);
                                j -= 1;
                            }
                        } else {
                            //Logic on a condition where the similar Folder already exists
                            File file = new File(rootLocation);
                            File[] listOfFiles = file.listFiles();
                            for(int i = 0; i < listOfFiles.length;i++){
                                if(listOfFiles[i].getName().equals(current)){
                                    File file1 = new File(rootLocation + "\\" + current + "\\" + files.get(j).name() + files.get(j).fileExtension());
                                    Files.copy(Path.of(files.get(j).path()), file1.toPath());
                                    new File(files.get(j).path()).delete();
                                    files.remove(j);
                                    j -= 1;
                                }
                            }
                            previousFileIndex = j;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("File already created!");
            }
        }
    }
}

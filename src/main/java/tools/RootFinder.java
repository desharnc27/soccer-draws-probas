package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desharnc27
 */
public class RootFinder {

    //private static String ROOT_NAME; 
    private static String rootPath;
    private static final String[] leafSeqs = new String[]{
        "target" + File.separator + "classes" + File.separator,
        "src" + File.separator + "main" + File.separator,
        "src" + File.separator + "test" + File.separator
    };

    /*public static String [] fileToArray(String filename){
        filename = rootPath+"paramFiles"+File.separator+filename;
        return FileUtils.toStringArr(filename);
    }
    public static void arrayToFile(String filename, String [] arr){
        filename = rootPath+"paramFiles"+File.separator+filename;
        FileUtils.toFile(filename,arr);
    }*/
    public static void initialize() throws RootException {
        //ROOT_NAME =rootName;
        setRootPath();
    }

    private static void setRootPath() throws RootException {
        String path = System.getProperty("user.dir") + File.separator;
        //System.out.println(path);
        int currentIndex = -1;
        for (var seq : leafSeqs) {
            List<Integer> indexChoices = findWord(path, seq);
            int tempNC = indexChoices.size();
            if (tempNC > 1) {
                throw RootException.make(path, indexChoices.get(0), indexChoices.get(1));
            } else if (tempNC == 1) {
                if (currentIndex >= 0) {
                    throw RootException.make(path, indexChoices.get(0), currentIndex);
                }
                currentIndex = indexChoices.get(0);
            }

        }
        if (currentIndex < 0) {
            rootPath = path;
        } else {
            rootPath = path.substring(0, currentIndex);
        }

        //int index = path.indexOf(ROOT_NAME);
        System.out.println("Root was set to: " + rootPath);
    }

    public static String getRootPath() {
        if (rootPath == null)
            try {
            setRootPath();
        } catch (RootException ex) {
            System.out.println("ta mere");
        }
        return rootPath;
    }

    //from https://www.baeldung.com/java-indexof-find-string-occurrences
    public static List<Integer> findWord(String textString, String word) {
        List<Integer> indexes = new ArrayList<>();
        String lowerCaseTextString = textString.toLowerCase();
        String lowerCaseWord = word.toLowerCase();

        int index = 0;
        while (index != -1) {
            index = lowerCaseTextString.indexOf(lowerCaseWord, index);
            if (index != -1) {
                indexes.add(index);
                index++;
            }
        }
        return indexes;
    }

    public static String getExtension(String filename) {
        System.out.println("filename: " + filename);
        int idx = filename.lastIndexOf(".");
        if (idx < 0) {
            return "";
        }
        return filename.substring(idx + 1);
    }

    public static String getExtension(File f) {
        if (f.isDirectory()) {
            return "";
        }
        return getExtension(f.getName());
    }

    public static int getLeafIndex(File f) {
        return f.getPath().length() - f.getName().length();
    }

    public static String[] separateLeaf(File f) {
        int sep = getLeafIndex(f);
        String path = f.getPath();
        return new String[]{path.substring(0, sep), path.substring(sep)};
    }

    /**
     * Takes a file, then returns a String representation of its path after
     * changing to specific extension
     *
     * @param file a file object
     * @param ext the extension to force
     * @return the string representation of the corrected path
     */
    public static String imposeExtension(File file, String ext) {
        String[] compon = separateLeaf(file);
        int dotIdx = compon[1].indexOf(".");
        String str;
        if (dotIdx < 0) {
            str = compon[1];
        } else {
            str = compon[1].substring(0, dotIdx);
        }
        str += "." + ext;
        return compon[0] + str;
    }

    public static void main(String[] args) {
        String path = "twado" + File.separator + "lan.ch.uk";
        File f = new File(path);
        System.out.println(imposeExtension(f, "kek"));
    }

}

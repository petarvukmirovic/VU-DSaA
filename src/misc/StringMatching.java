package misc;

import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StringMatching {
    public static class KMPAlgorithm {
        private final String _pattern;
        int[] _prefixFunction;

        public KMPAlgorithm(String pattern){
            this._pattern = pattern;
            _createPrefixFunction();
        }

        private void _createPrefixFunction() {
            _prefixFunction = new int[_pattern.length()];
            _prefixFunction[0] = -1; // Java indices start from 0, thus the change

            int lastPrefix = -1;
            for(int i=1; i<_pattern.length(); i++){
                while(lastPrefix > -1
                        && _pattern.charAt(lastPrefix+1) != _pattern.charAt(i)){
                    // As long as we cannot "extend" the current prefix so
                    // that it is equal to the corresponding suffix we try
                    // with the smaller prefix until we run out of all prefixes
                    lastPrefix = _prefixFunction[lastPrefix];
                }

                if (_pattern.charAt(lastPrefix+1) == _pattern.charAt(i)){
                    // We can "extend" the current prefix
                    lastPrefix++;
                }

                _prefixFunction[i] = lastPrefix;
            }
        }

        public int matchAgainst(String text){
            int patIdx = -1;
            for(int textIdx=0; textIdx<text.length(); textIdx++){
                while(patIdx >-1
                        && _pattern.charAt(patIdx+1) != text.charAt(textIdx)){
                    // Mismatch found, slide the prefix
                    patIdx = _prefixFunction[patIdx];
                }

                if (_pattern.charAt(patIdx+1) == text.charAt(textIdx)){
                    patIdx++;
                }

                if (patIdx == _pattern.length()-1){
                    // We have found the pattern in the text
                    return textIdx-_pattern.length()+1;
                }
            }

            return -1;
        }
    }

    public static void main(String[] args){
        KMPAlgorithm KMP = new KMPAlgorithm("aaab");

        System.out.println(KMP.matchAgainst("aacaadaaba"));
        System.out.println(KMP.matchAgainst("aacaadaaaba"));
        System.out.println(KMP.matchAgainst("aacaaadaaabaaab"));
        System.out.println(KMP.matchAgainst("aacaaadaabaaab"));

        KMPAlgorithm KMPExercisesA = new KMPAlgorithm("aabaaa");
        KMPAlgorithm KMPExercisesB = new KMPAlgorithm("000101");

        System.out.println(KMPExercisesA.matchAgainst("aaabaadaabaaa"));
        System.out.println(KMPExercisesB.matchAgainst("000010001010001"));

        // Worst case
        int PATTERN_LEN = 1000;
        String pattern = new String(new char[PATTERN_LEN-1]).replace('\0', 'a') + "b";

        int TEXT_LEN = 10000000;
        String text = new String(new char[TEXT_LEN-1]).replace('\0', 'a') + "b";

        long start = System.nanoTime();
        int res = new KMPAlgorithm(pattern).matchAgainst(text);
        long end = System.nanoTime();

        System.out.println("KMP ALGORITHM: " + res + "\n" +
                "TIME: " + (end-start)/10e8);

        start = System.nanoTime();
        // Java uses naive string matching
        // (slightly improved though)
        res = text.indexOf(pattern);
        end = System.nanoTime();

        System.out.println("NAIVE ALGORITHM: " + res + "\n" +
                "TIME: " + (end-start)/10e8);
    }
}

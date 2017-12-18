package com.example.jtuul.mult;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtuul on 12/2/2017.
 * Tama luokka pitää sisällään kaikki pelin logiikkaan tarvittavat muuttujat ja metodit.
 *
 * Uudeksi paikaksi arvotaan aina alas tai oikea,
 * Paitsi jos edellisellä kierroksella tultiin uuteen ruutuun ja se ei mennyt target-nopeudella
 * Tällöin valitaan se ruutu, joka on lähinnä target aikaa
 *
 * TODO: Pystyisikö suoritusta nopeuttamaan, uusi thread? Petrin kanssa?
 * TODO: Tee aloitusruutuaktiviteetti ja siihen nappulta jne. (sama layout)
 * TODO: Jostakin syystä voi vielä tulla oikea vastaus kahteen kertaan. Lisäksi täysin random-vastauksen voisi poistaa.
 *
 */

public class MultiplicationGame {
    // Pitaisikö rakentaa testiluokka, jossa tätä tehdään?
    int x; int x0, x1, xLen;
    int y; int y0, y1, yLen;

    public Integer answer;
    final public int initialAnswerTime = 10 * 1000; // Annettava millisekunteissa, jotta vältytään double lukujen vertaillulta!
    private boolean alreadyOnNew = false;
    // int[][] answerTimeMatrix = new int[10][10];
    public int[][] answerTimeMatrix;
    private long answerStartTime = 0;
    private double answerTime;
    public double answerTargetTime = 5 * 1000; // Mikä on vastaukselle tavoite aika
    SoundPlayer sp;

    public MultiplicationGame(int x0, int x1, int y0, int y1) { // Konstruktori

        this.x0 = x0; this.x1 = x1; this.y0 = y0; this.y1 = y1;
        this.x = x0; this.y = y0;
        this.setxAndyLen();
        answerTimeMatrix = new int[xLen][yLen];
        this.initializeMatrix(answerTimeMatrix, initialAnswerTime);
        // int[][] relMatrix = new int[xLen][yLen];
        // this.initializeMatrix(relMatrix, 0);
        int xx = 1;
    }

    private int test(int x, int y) { // Taman voi myohemmin poistaa
        return x * y;
    }

    public Integer[] getAnswerOptions() {

        Integer[] wrongAnswers = new Integer[11];
        Integer[] answersShown = new Integer[4];

        // Tee ensin kymmenen vaihtoehtoa, joista vain yksi oikein
        Double rnd1 = Math.floor(Math.random() * this.x1*this.y1);
        Double rnd2 = Math.floor(Math.random() * 10 - 5);
        wrongAnswers[0] = rnd1.intValue();
        wrongAnswers[1] = rnd1.intValue() + this.x * this.y;
        wrongAnswers[2] = rnd2.intValue() + this.x * this.y;
        wrongAnswers[3] = this.x * this.y + this.y;
        wrongAnswers[4] = this.x * this.y - this.y;
        wrongAnswers[5] = this.x * this.y + this.x;
        wrongAnswers[6] = this.x * this.y - this.x;
        wrongAnswers[7] = this.x * this.y + 1;
        wrongAnswers[8] = this.x * this.y - 1;
        wrongAnswers[9] = this.x * this.y + 2;
        wrongAnswers[10] = this.x * this.y - 2;

        answersShown[3] = x * y; // Varmistetaan, ettei tule oikea vastaus tuplana (kts. identical())
        for (int i = 0; i <= 3; ++i) {
            answersShown[i] = wrongAnswers[this.getRandomInt(0, 10)];
            boolean cond = true;
            int n = 0; int addOn = 0;
            while ((answersShown[i] < x0*y0 | answersShown[i] > x1*y1 | cond) & x1*y1-x0*y0 > 2) {
                n = n+1;
                Double tmp = Math.floor(Math.random() * 10 - 5);
                if(n > 10) addOn = tmp.intValue(); // Varmistetaan, että löytyy sopiva väärä vastaus
                answersShown[i] = wrongAnswers[this.getRandomInt(0, 9)] + addOn;
                cond = this.identical(answersShown);
                if(n > 100)
                    cond = true;
            }
        }
        answersShown[this.getRandomInt(0, 3)] = x * y; // Correct answer
        return answersShown;
    }

    private int getRandomInt(int minValue, int maxValue) {
        Double tmp = Math.floor(Math.random() * (maxValue+1) + minValue);
        if(tmp.intValue() > maxValue) this.catchFun();
        if(tmp.intValue() < minValue) this.catchFun();
        return tmp.intValue();
    }

    private void setx1y1AnswerTime() {
        if(this.answerTimeMatrix[0][0] >= this.initialAnswerTime) this.answerTimeMatrix[0][0] = this.answerTimeMatrix[0][0] * this.initialAnswerTime;
    }

    private List getAccessablePositions(String direction) {
        // Tän voisi tehdä indexOf avulla siten, että tehdään matriisi, jossa ao. ehto ja sen jälkeen etsitään listaan kuten alempana ehdon täyttävät indeksit
        // xLen = this.setxAndyLen("x");
        // yLen = this.setxAndyLen("y");
        List<Integer> ret = new ArrayList<Integer>();

        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if(this.answerTimeMatrix[i][k] > this.answerTargetTime & this.answerTimeMatrix[i][k] < this.initialAnswerTime) {
                    if(direction.equals("x")) ret.add(i);
                    if(direction.equals("y")) ret.add(k);
                }
            }
        }
        return ret;
    }

    private void catchFun() {
        int sxxx = 1; // Tähän myöhemmin error catch
    }

    private void setSmallestNewAsxAndy() {
        // Etsitään pienimmän tekemättömän (uuden) kertolaskun indeksi
        // xLen = this.setxAndyLen("x");
        // yLen = this.setxAndyLen("y");
        List<Integer> posInd = new ArrayList<Integer>();

        int current = x1*y1; // Aloitetaan suurimmasta mahdollisesta
        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if (this.answerTimeMatrix[i][k] == this.initialAnswerTime & (k * i) < current) { // xxx tässä pitää vielä valita näistä pienin
                    current = k * i; // Vrt. if-lauseen sisällä oleva ehto; k*i "koko" ei mene loop-järjestyksessä
                    posInd.add(i);
                    posInd.add(k);
                }
            }
        }

        if (posInd.size() > 0) {
            this.x = (int) posInd.get(posInd.size() - 2) + x0;
            this.y = (int) posInd.get(posInd.size() - 1) + y0;
            if(this.x > x1 | this.x < x0) this.catchFun();
            if(this.y > y1 | this.y < y0) this.catchFun();
            this.alreadyOnNew = true; // Arvottu paikka ei koskaan ole uusi
        } else {
            this.catchFun(); // Tähän myöhemmin pelin lopetus, kaikki laskut on suoritettu alle targetTimen
        }
    }

    // ---> setRandomxAndy AND ---> setCrawlxAndy.
    private void setRandomxAndy() {
        // this.sp.playSound(3);
        List xList = this.getAccessablePositions("x");
        List yList = this.getAccessablePositions("y");
        if(xList.size() == 0 | yList.size() == 0) { // Jos ei onnistu löytää yhtään jo laskettua laskua, joka on vielä yli targetin, niin valitaan lasku, joka on pienin vielä suorittamaton
            this.setSmallestNewAsxAndy();
        } else {
            int xPos = this.getRandomInt(0, xList.size()-1);
            int yPos = this.getRandomInt(0, yList.size()-1);

            this.x = (int) xList.get(xPos) + x0;
            this.y = (int) yList.get(yPos) + y0;
            if(this.x > x1 | this.x < x0) this.catchFun();
            if(this.y > y1 | this.y < y0) this.catchFun();
            this.alreadyOnNew = false; // Arvottu paikka ei koskaan ole uusi
        }
    }

    // ---> setRandomxAndy
    private void setCrawlxAndyByIndex(int index, int up, int down, int left, int right) {
        boolean cond = true;
        int xx = this.x;
        int yy = this.y;
        int n = 0;
        while (cond) {
            n = n + 1;
            switch (index) { // Asetetaan uusi arvo, joka vastaa matalinta arvoa (joka suurempi kuin target value)
                case 0:
                    this.setRandomxAndy();
                    break;
                case 1:
                    this.x = up+x0;
                    break;
                case 2:
                    this.x = down+x0;
                    break;
                case 3:
                    this.y = left+y0;
                    break;
                case 4:
                    this.y = right+y0;
                    break;
            }
            if(this.y != yy | this.x != xx) cond = false;
            if(this.xLen == 1 & this.yLen == 1) cond = false; // Vain yksi solu, erikoistapaus..
            index = this.getRandomInt(0, 4);
        }
        if(this.x > x1)
            x0 = -1;
        if(this.y > y1)
            x0 = -1;
        if(this.x < x0)
            x0 = -1;
        if(this.y < y0)
            x0 = -1;

    }

    private int getxPos() { return this.x - x0; }
    private int getyPos() { return this.y - y0; }

    // ---> setRandomxAndy (via setCrawlxAndyByIndex)
    private void setCrawlxAndy() {
        int up = Math.max(getxPos()-1, 0);
        int down = Math.min(getxPos()+1, xLen-1);
        int left = Math.max(0, getyPos()-1);
        int right = Math.min(yLen-1, getyPos()+1);
        int[] moveVector = new int[5];

        // Eri suunnat kentällä
        moveVector[0] = this.answerTimeMatrix[this.x-x0][this.y-y0];
        moveVector[1] = this.answerTimeMatrix[up][getyPos()];
        moveVector[2] = this.answerTimeMatrix[down][getyPos()];
        moveVector[3] = this.answerTimeMatrix[getxPos()][left];
        moveVector[4] = this.answerTimeMatrix[getxPos()][right];

        List indLargest = this.getIndecesOfLargest(moveVector); // Etsitään missä suunnassa on suurin arvo (voi olla vielä suorittamaton ruutu)
        if(indLargest.size() > 0) { // Löytyi uusi paikka --> mennään jompaan kumpaan niistä (tai reunalla vain yksi vaihtoehto)
            int movePos = (int) this.getRandomInt(0, indLargest.size()-1);

            this.setCrawlxAndyByIndex((int) indLargest.get(movePos), up, down, left, right);
        } else { // Ei löytynyt mitään uutta kertolaskuparia ---> etsitään parhaiten aikaisemmin osattu
            List indSmallest = this.getIndecesOfSmallest(moveVector);

            if(indSmallest.size() == 0) this.catchFun();

            int movePos = this.getRandomInt(0, indSmallest.size()-1);
            this.setCrawlxAndyByIndex(movePos, up, down, left, right);
        }

        if(this.x > x1 | this.x < x0) this.catchFun();
        if(this.y > y1 | this.y < y0) this.catchFun();

        this.alreadyOnNew = this.onNew();
    }

    // ---> setRandomxAndy AND ---> setCrawlxAndy
    private void setNewxAndy() {
        if(this.alreadyOnNew) { // Oltiin edellisellä kierroksella jo uuden päällä
            if (this.answerTime > this.answerTargetTime) { // ja aikaa kului liikaa -> arvotaan
                this.setRandomxAndy();
            } else {
                this.setCrawlxAndy(); // Crawl voi päätyä uuteen
            }
        } else {
            this.setCrawlxAndy(); // Crawl voi päätyä uuteen
        }
        if(this.x > x1 | this.x < x0) this.catchFun();
        if(this.y > y1 | this.y < y0) this.catchFun();

    }

    private boolean onNew() {
        return this.answerTimeMatrix[this.x-x0][this.y-y0] == this.initialAnswerTime;
        // return isOnNew;
    }

    private List getIndecesOfSmallest(int[] array ) {
        List<Integer> smallest = new ArrayList<Integer>();
        if ( array == null || array.length == 0 ) return smallest; // null or empty

        smallest.add(0); // @0 index; Pienin indeksi ehdotus on ensin 0, testataan sen jälkeen löytyykö pienempää?
        for ( int i = 1; i < array.length; i++ ) {
            if ( array[i] <= array[smallest.get(smallest.size()-1)] & array[i] > this.answerTargetTime) smallest.add(i); // Mennään pienimpään, jos ei uutta vieressä, ei kuitenkaan jo suoritettuun
        }
        return smallest; // position of the first largest found
    }

    private List getIndecesOfLargest(int[] array ) {
        List<Integer> largest = new ArrayList<Integer>();
        if ( array == null || array.length == 0 ) return largest; // null or empty

        largest.add(0); // @0 index; Suurin indeksi ehdotus on ensin 0, testataan sen jälkeen löytyykö pienempää?
        for ( int i = 1; i < array.length; i++ ) {
            if ( array[i] >= array[largest.get(largest.size()-1)] & array[i] > this.answerTargetTime) largest.add(i); // Mennään pienimpään, jos ei uutta vieressä, ei kuitenkaan jo suoritettuun
        }

        int biggest = array[largest.get(largest.size()-1)];
        return indexOfAll(biggest, array); // Palauttaa kaikki suurimmat indeksit
    }

    private static ArrayList<Integer> indexOfAll(int biggest, int[] array){
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++)
            if(biggest == array[i])
                indexList.add(i);
        return indexList;
    }

    public static <T> boolean contains(final T[] array, final T v) {
        if (v == null) {
            for (final T e : array)
                if (e == null)
                    return true;
        } else {
            for (final T e : array)
                if (e == v || v.equals(e))
                    return true;
        }

        return false;
    }

    public String getxTimesYStr() { // Taman pitaa olla public
        return String.valueOf(this.x) + " x " + String.valueOf(this.y);
    }

    private boolean identical(Integer[] a) {
        boolean ret = false;
        if (a.length != 1) {
            for (int i = 0; i < a.length; i++) {
                for (int k = i + 1; k < a.length; k++) {
                    if (a[i] != null & a[k] != null) {
                        if (a[i].equals(a[k])) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void initializeMatrix(int[][] matrix, int value) {
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[0].length; k++) {
                matrix[i][k] = (int) value;
            }
        }
    }

    public Integer getCorrectAnswer() {
        return this.x * this.y;
    }

    public void evaluateAnswer() {
        long answerEndTime = System.currentTimeMillis();
        this.answerTime = (answerEndTime - this.answerStartTime);
        if(this.answer.equals(this.getCorrectAnswer())) {
            int prevAnswerTime = this.answerTimeMatrix[this.x-x0][this.y-y0];
            int newTime = (int) Math.min((this.answerTime + prevAnswerTime) / 2, this.initialAnswerTime);
            this.answerTimeMatrix[this.x-x0][this.y-y0] = newTime;
            if(newTime > this.answerTargetTime) this.sp.playSound(1);
            if(newTime <= this.answerTargetTime) this.sp.playSound(3);
        } else { // Vaara vastaus
            this.sp.playSound(2);
            this.answerTime = this.answerTime + this.initialAnswerTime;
            this.answerTimeMatrix[this.x-x0][this.y-y0] = (int) Math.min(this.answerTime, this.initialAnswerTime);
        }


        this.checkEndCondition();
    }

    public void setxAndyLen() {
        this.xLen = x1-x0+1;
        this.yLen = y1-y0+1;
    }

    public void newRound(boolean notFirstRound) {
        this.answerStartTime = System.currentTimeMillis();
        if(!notFirstRound) this.x = x0; this.y = y0;
        if(notFirstRound) this.setNewxAndy();

    }

    public void checkEndCondition() {
        List<Integer> ret = new ArrayList<Integer>();
        boolean endIt = true;
        for (int i = 0; i < xLen; i++) {
            for (int k = 0; k < yLen; k++) {
                if(this.answerTimeMatrix[i][k] > this.answerTargetTime) { // Ovatko kaikki vastaukset tavoitetta nopeammat?
                    endIt = false;
                    break;
                }
            }
        }
        if(endIt) {
            sp.playSound(4);
            this.initializeMatrix(answerTimeMatrix, initialAnswerTime);
        }
    }

    public void setSoundPlayer(Activity act) {
        sp = new SoundPlayer(act);
    }
}
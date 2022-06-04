package it.polimi.ingsw.client;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.Character5;

import java.util.Formatter;

public class ViewCLI extends View<ClientCLI> {
    private final String OS = System.getProperty("os.name").toLowerCase();

    public ViewCLI(ClientCLI client) {
        this.client = client;
    }

    public boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0);
    }

    public boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Print title
     */
    public void printTitle() {
        String eryantis = "\u001b[1;91m░░░░░░░░░░ ░░░░░░░    ░░░   ░░░░   ░░░░░    ░░░  ░░░░░ ░░░░░░░░░░  ░░░░░░░░     ░░░ ░░  \n" +
                "░░░░░░░░░░ ░░░░░░░░   ░░░   ░░░░   ░░░░░    ░░░░ ░░░░░ ░░░░░░░░░░  ░░░░░░░░    ░░░░░░░  \n" +
                "\u001b[1;93m ░░     ░░  ░░   ░░░   ░░    ░░      ░░░     ░░░   ░░  ░░  ░░  ░░     ░░      ░░    ░░  \n" +
                " ░░  ░░ ░░  ░░    ░░    ░░  ░░      ░░ ░░    ░░░░  ░░  ░░  ░░  ░░     ░░      ░░    ░░  \n" +
                "\u001b[1;92m ░░░░░░     ░░   ░░░     ░░░░       ░░ ░░    ░░ ░  ░░  ░░  ░░  ░░     ░░      ░░░░      \n" +
                " ░░░░░░     ░░░░░░░      ░░░░      ░░   ░░   ░░ ░░ ░░      ░░         ░░       ░░░░░░   \n" +
                "\u001b[1;94m ░░  ░░     ░░░░░░        ░░       ░░░░░░░   ░░  ░░░░      ░░         ░░          ░░░░  \n" +
                " ░░     ░░  ░░  ░░░       ░░       ░░░░░░░   ░░  ░░░░      ░░         ░░      ░░    ░░  \n" +
                "\u001b[1;95m ░░     ░░  ░░   ░░░      ░░      ░░     ░░  ░░   ░░░      ░░         ░░      ░░    ░░  \n" +
                "░░░░░░░░░░ ░░░░░  ░░░   ░░░░░░   ░░░░   ░░░░░░░░░ ░░░    ░░░░░░    ░░░░░░░░   ░░░░░░░   \n" +
                "░░░░░░░░░░ ░░░░░   ░░   ░░░░░░   ░░░░   ░░░░░░░░░  ░░    ░░░░░░    ░░░░░░░░   ░░ ░░░    \u001b[0m\n";
        try {
            new ProcessBuilder("cmd", "/c", "echo " + eryantis).inheritIO().start().waitFor();
            new Object().wait(5000);
            if (isWindows()) {
                try {
                    new ProcessBuilder("cmd", "/c", "clear").inheritIO().start().waitFor();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (isUnix()) {
                //clean screen in Unix
            } else if (isMac()) {
                //clean screen in Mac
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Print the description of the elements of the game interface
     */
    public void printDescription() {
        String description = "\u001b[97mGame elements:\n" +
                             "\u001b[91m● \u001b[93m● \u001b[92m● \u001b[94m● \u001b[95m● → \u001b[97mstudents\n" +
                             "\u001b[90m█ \u001b[97█ \u001b[38;5;247m█ → \u001b[97mtowers\n" +
                             "\u001b[91m■ \u001b[93m■ \u001b[92m■ \u001b[94m■ \u001b[95m■ → \u001b[97mprofessors\n" +
                             "♦ → mother nature\n" +
                             "\u001b[31mꞳ → \u001b[97mno entry\n" +
                             "\u001b[92m$ → \u001b[97mcoin\u001b[0m";
        try {
            new ProcessBuilder("cmd", "/c", "echo " + description).inheritIO().start().waitFor();
            new Object().wait(15000);
            if (isWindows()) {
                try {
                    new ProcessBuilder("cmd", "/c", "clear").inheritIO().start().waitFor();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else if (isUnix()) {
                //clean screen in Unix
            } else if (isMac()) {
                //clean screen in Mac
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Print view
     */
    @Override
    public void print() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        String moveCursor;
        int counter, curRow, curColumn;

        if (isWindows()) {
            try {
                new ProcessBuilder("cmd", "/c", "clear").inheritIO().start().waitFor();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (isUnix()) {
            //clean screen in Unix
        } else if (isMac()) {
            //clean screen in Mac
        }

        //Student(●)|Tower(█)|Professor(■)|MotherNature(♦)|NoEntry(Ꭓ)|Coin($)
        //Print islands
        moveCursor = "\u001b[36;1H";
        curRow = 37;
        counter = 1;
        formatter.format(moveCursor + "\u001b[97mISLANDS ↓\u001b[0m\n");
        for (Island island : islands) {
            moveCursor = "\u001b[" + curRow + ";1H";
            formatter.format(moveCursor + "\u001b[97m" + counter + "°ISLAND →\u001b[0m");
            for (Student student : island.getStudents()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●\u001b[0m");
                } else {
                    formatter.format("\u001b[1;95m ●\u001b[0m");
                }
            }
            for (Tower tower : island.getTowers()) {
                if (tower.getColor().equals(TowerColor.BLACK)) {
                    formatter.format("\u001b[1;90m █\u001b[0m");
                } else if (tower.getColor().equals(TowerColor.WHITE)) {
                    formatter.format("\u001b[1;97m █\u001b[0m");
                } else {
                    formatter.format("\u001b[38;5;247m █\u001b[0m");
                }
            }
            if (posMotherNature == counter - 1) {
                formatter.format("\u001b[97m ♦\u001b[0m");
            }
            if ((expert) && (island.getNoEntry() > 0)) {
                formatter.format("\u001b[1;31m Ꭓ\u001b[0m\n");
            } else {
                formatter.format("\n");
            }
            ++counter;
            ++curRow;
        }

        //Print clouds
        moveCursor = "\u001b[1;1H";
        curRow = 2;
        counter = 1;
        formatter.format(moveCursor + "\u001b[97mCLOUDS ↓\u001b[0m\n");
        for (Cloud cloud : clouds) {
            moveCursor = "\u001b[" + curRow + ";1H";
            formatter.format(moveCursor + "\u001b[97m" + counter + "°CLOUD →\u001b[0m");
            for (Student student : cloud.getStudents()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●\u001b[0m");
                } else {
                    formatter.format("\u001b[1;95m ●\u001b[0m");
                }
            }
            formatter.format("\n");
            ++counter;
            ++curRow;
        }

        //Print characters
        if (expert) {
            moveCursor = "\u001b[1;161H";
            formatter.format(moveCursor + "\u001b[97mCHARACTERS ↓\u001b[0m\n");
            curRow = 2;
            for (Character character : characters) {
                moveCursor = "\u001b[" + curRow + ";161H";
                formatter.format(moveCursor + "\u001b[97m%1$d°CHARACTER →  COST: %2$d\u001b[0m", character.getId() + 1, character.getCost());
                if (character instanceof StudentCharacter) {
                    formatter.format("\u001b[97m  STUDENTS:\u001b[0m");
                    for (Student student : ((StudentCharacter) character).getStudents()) {
                        if (student.getColor().equals(PawnColor.RED)) {
                            formatter.format("\u001b[1;91m ●\u001b[0m");
                        } else if (student.getColor().equals(PawnColor.YELLOW)) {
                            formatter.format("\u001b[1;93m ●\u001b[0m");
                        } else if (student.getColor().equals(PawnColor.GREEN)) {
                            formatter.format("\u001b[1;92m ●\u001b[0m");
                        } else if (student.getColor().equals(PawnColor.BLUE)) {
                            formatter.format("\u001b[1;94m ●\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;95m ●\u001b[0m");
                        }
                    }
                }
                if (character instanceof Character5) {
                    formatter.format("\u001b[97m  NO ENTRY:\u001b[0m");
                    for (counter = 1; counter <= ((Character5) character).getNoEntry(); ++counter) {
                        formatter.format("\u001b[1;91m Ꭓ\u001b[0m");
                    }
                }
                formatter.format("\n");
                ++curRow;
            }
        }

        //Print character descriptions
        curRow = 51;
        curColumn = 1;
        moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
        formatter.format(moveCursor);
        for (Character character : characters) {
            formatter.format("%1$d°CHARACTER DESCRIPTION → %2$s\n", character.getId(), character.getDescription());
        }

        //Print assistants
        moveCursor = "\u001b[8;1H";
        curRow = 9;
        formatter.format(moveCursor + "\u001b[97mYOUR ASSISTANTS ↓\u001b[0m\n");
        for (Assistant assistant : assistants) {
            moveCursor = "\u001b[" + curRow + ";1H";
            formatter.format("\u001b[97mValue: %1$d  Moves: %2$d\u001b[0m\n", assistant.getValue(), assistant.getMoves());
            ++curRow;
        }

        //Print current assistants
        moveCursor = "\u001b[8;81H";
        curRow = 9;
        formatter.format(moveCursor + "\u001b[97mASSISTANTS PLAYED IN THIS ROUND ↓\u001b[0m\n");
        for (Player player : playersOrder) {
            moveCursor = "\u001b[" + curRow + ";81H";
            if (player.getCurrentAssistant() == null) {
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOU HAVEN'T PLAYED AN ASSISTANT YET\u001b[0m\n");
                } else {
                    formatter.format(moveCursor + "\u001b[97m%1$s HASN'T PLAYED AN ASSISTANT YET\u001b[0m\n", player.getName().toUpperCase());
                }
            } else {
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOU HAVE PLAYED AN ASSISTANT →  VALUE: %1$d  MOVES: %2$d\u001b[0m\n", player.getCurrentAssistant().getValue(), player.getCurrentAssistant().getMoves());
                } else {
                    formatter.format(moveCursor + "\u001b[97m%1$s HAS PLAYED AN ASSISTANT →  VALUE: %2$d  MOVES: %3$d\u001b[0m\n", player.getName().toUpperCase(), player.getCurrentAssistant().getValue(), player.getCurrentAssistant().getMoves());
                }
            }
            ++curRow;
        }

        //Print discard piles
        moveCursor = "\u001b[8;161H";
        curRow = 9;
        formatter.format(moveCursor + "\u001b[97mDISCARD PILES ↓\u001b[0m\n");
        for (Player player : playersOrder) {
            moveCursor = "\u001b[" + curRow + ";161H";
            if (player.getDiscardPile() == null) {
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOUR DISCARD PILE IS EMPTY\u001b[0m\n");
                } else {
                    formatter.format(moveCursor + "\u001b[97m%1$s'S DISCARD PILE IS EMPTY\u001b[0m\n", player.getName().toUpperCase());
                }
            } else {
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOUR LAST ASSISTANT PLAYED →  VALUE: %2$d  MOVES: %3$d\u001b[0m\n", player.getDiscardPile().getValue(), player.getDiscardPile().getMoves());
                } else {
                    formatter.format(moveCursor + "\u001b[97mLAST ASSISTANT PLAYED BY %1$s →  VALUE: %2$d  MOVES: %3$d\u001b[0m\n", player.getName().toUpperCase(), player.getDiscardPile().getValue(), player.getDiscardPile().getMoves());
                }
            }
            ++curRow;
        }

        //Print players' schools
        curColumn = 1;
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                curRow = 23;
                moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOUR SCHOOL ↓\u001b[0m\n");
                } else {
                    formatter.format(moveCursor + "\u001b[97m" + player.getName().toUpperCase() + "'S SCHOOL ↓\u001b[0m\n");
                }
                ++curRow;
                moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                formatter.format(moveCursor + "\u001b[97mENTRANCE →\u001b[0m");
                for (Student student : player.getSchool().getEntrance()) {
                    if (student.getColor().equals(PawnColor.RED)) {
                        formatter.format("\u001b[1;91m ●\u001b[0m");
                    } else if (student.getColor().equals(PawnColor.YELLOW)) {
                        formatter.format("\u001b[1;93m ●\u001b[0m");
                    } else if (student.getColor().equals(PawnColor.GREEN)) {
                        formatter.format("\u001b[1;92m ●\u001b[0m");
                    } else if (student.getColor().equals(PawnColor.BLUE)) {
                        formatter.format("\u001b[1;94m ●\u001b[0m");
                    } else {
                        formatter.format("\u001b[1;95m ●\u001b[0m");
                    }
                }
                formatter.format("\n");
                ++curRow;
                for (PawnColor pawncolor : player.getSchool().getTables().keySet()) {
                    moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                    formatter.format(moveCursor + "\u001b[97m" + pawncolor.toString().toUpperCase() + " TABLE →\u001b[0m");
                    counter = 1;
                    for (Student student : player.getSchool().getTables().get(pawncolor)) {
                        if (pawncolor.equals(PawnColor.RED)) {
                            if (counter == 3 || counter == 6 || counter == 9) {
                                formatter.format("\u001b[1;91m ◙\u001b[0m");
                            } else {
                                formatter.format("\u001b[1;91m ●\u001b[0m");
                            }
                        } else if (pawncolor.equals(PawnColor.YELLOW)) {
                            if (counter == 3 || counter == 6 || counter == 9) {
                                formatter.format("\u001b[1;93m ◙\u001b[0m");
                            } else {
                                formatter.format("\u001b[1;93m ●\u001b[0m");
                            }
                        } else if (pawncolor.equals(PawnColor.GREEN)) {
                            if (counter == 3 || counter == 6 || counter == 9) {
                                formatter.format("\u001b[1;92m ◙\u001b[0m");
                            } else {
                                formatter.format("\u001b[1;92m ●\u001b[0m");
                            }
                        } else if (pawncolor.equals(PawnColor.BLUE)) {
                            if (counter == 3 || counter == 6 || counter == 9) {
                                formatter.format("\u001b[1;94m ◙\u001b[0m");
                            } else {
                                formatter.format("\u001b[1;94m ●\u001b[0m");
                            }
                        } else {
                            if (counter == 3 || counter == 6 || counter == 9) {
                                formatter.format("\u001b[1;95m ◙\u001b[0m");
                            } else {
                                formatter.format("\u001b[1;95m ●\u001b[0m");
                            }
                        }
                        ++counter;
                    }
                    formatter.format("\n");
                    ++curRow;
                }
                moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                formatter.format(moveCursor + "\u001b[97mPROFESSORS →\u001b[0m");
                for (Professor professor : player.getSchool().getProfessors()) {
                    if (professor.getColor().equals(PawnColor.RED)) {
                        formatter.format("\u001b[1;91m ■\u001b[0m");
                    } else if (professor.getColor().equals(PawnColor.YELLOW)) {
                        formatter.format("\u001b[1;93m ■\u001b[0m");
                    } else if (professor.getColor().equals(PawnColor.GREEN)) {
                        formatter.format("\u001b[1;92m ■\u001b[0m");
                    } else if (professor.getColor().equals(PawnColor.BLUE)) {
                        formatter.format("\u001b[1;94m ■\u001b[0m");
                    } else {
                        formatter.format("\u001b[1;95m ■\u001b[0m");
                    }
                }
                formatter.format("\n");
                if (playersOrder.size() != 4) {
                    ++curRow;
                    moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                    formatter.format(moveCursor + "\u001b[97m" + player.getName().toUpperCase() + "'S TOWERS →\u001b[0m");
                    for (Tower tower : team.getTowers()) {
                        if (tower.getColor().equals(TowerColor.BLACK)) {
                            formatter.format("\u001b[1;90m █\u001b[0m");
                        } else if (tower.getColor().equals(TowerColor.WHITE)) {
                            formatter.format("\u001b[1;97m █\u001b[0m");
                        } else {
                            formatter.format("\u001b[38;5;247m █\u001b[0m");
                        }
                    }
                }
                curColumn += 60;
            }
            if (playersOrder.size() == 4) {
                curRow += 2;
                moveCursor = "\u001b[" + curRow + ";" + (curColumn - 100) + "H";
                formatter.format(moveCursor + "%1$s AND %2$s'S TOWERS →", team.getPlayers().get(0), team.getPlayers().get(1));
                for (Tower tower : team.getTowers()) {
                    if (tower.getColor().equals(TowerColor.BLACK)) {
                        formatter.format("\u001b[1;90m █\u001b[0m");
                    } else if (tower.getColor().equals(TowerColor.WHITE)) {
                        formatter.format("\u001b[1;97m █\u001b[0m");
                    } else {
                        formatter.format("\u001b[38;5;247m █\u001b[0m");
                    }
                }
            }
        }

        /**
        for (Player player : playersOrder) {
            curRow = 26;
            moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
            if (player.getName().equals(client.getName())) {
                formatter.format(moveCursor + "\u001b[97mYOUR SCHOOL ↓\u001b[0m\n");
            } else {
                formatter.format(moveCursor + "\u001b[97m" + player.getName().toUpperCase() + "'S SCHOOL ↓\u001b[0m\n");
            }
            ++curRow;
            moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
            formatter.format(moveCursor + "\u001b[97mENTRANCE →\u001b[0m");
            for (Student student : player.getSchool().getEntrance()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●\u001b[0m");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●\u001b[0m");
                } else {
                    formatter.format("\u001b[1;95m ●\u001b[0m");
                }
            }
            formatter.format("\n");
            ++curRow;
            for (PawnColor pawncolor : player.getSchool().getTables().keySet()) {
                moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
                formatter.format(moveCursor + "\u001b[97m" + pawncolor.toString().toUpperCase() + " TABLE →\u001b[0m");
                counter = 1;
                for (Student student : player.getSchool().getTables().get(pawncolor)) {
                    if (pawncolor.equals(PawnColor.RED)) {
                        if (counter == 3 || counter == 6 || counter == 9) {
                            formatter.format("\u001b[1;91m ◙\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;91m ●\u001b[0m");
                        }
                    } else if (pawncolor.equals(PawnColor.YELLOW)) {
                        if (counter == 3 || counter == 6 || counter == 9) {
                            formatter.format("\u001b[1;93m ◙\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;93m ●\u001b[0m");
                        }
                    } else if (pawncolor.equals(PawnColor.GREEN)) {
                        if (counter == 3 || counter == 6 || counter == 9) {
                            formatter.format("\u001b[1;92m ◙\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;92m ●\u001b[0m");
                        }
                    } else if (pawncolor.equals(PawnColor.BLUE)) {
                        if (counter == 3 || counter == 6 || counter == 9) {
                            formatter.format("\u001b[1;94m ◙\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;94m ●\u001b[0m");
                        }
                    } else {
                        if (counter == 3 || counter == 6 || counter == 9) {
                            formatter.format("\u001b[1;95m ◙\u001b[0m");
                        } else {
                            formatter.format("\u001b[1;95m ●\u001b[0m");
                        }
                    }
                    ++counter;
                }
                formatter.format("\n");
                ++curRow;
            }
            moveCursor = "\u001b[" + curRow + ";" + curColumn + "H";
            formatter.format(moveCursor + "\u001b[97mPROFESSORS →\u001b[0m");
            for (Professor professor : player.getSchool().getProfessors()) {
                if (professor.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ■\u001b[0m");
                } else if (professor.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ■\u001b[0m");
                } else if (professor.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ■\u001b[0m");
                } else if (professor.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ■\u001b[0m");
                } else {
                    formatter.format("\u001b[1;95m ■\u001b[0m");
                }
            }
            formatter.format("\n");
            curColumn += 60;
        }
         **/

        //Print players' coin reserve
        if (expert) {
            moveCursor = "\u001b[1;81H";
            curRow = 2;
            formatter.format(moveCursor + "\u001b[97mCOIN RESERVES ↓\u001b[0m\n");
            for (Player player : playersOrder) {
                moveCursor = "\u001b[" + curRow + ";81H";
                if (player.getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOUR COIN RESERVE →\u001b[0m");
                } else {
                    formatter.format(moveCursor + "\u001b[97m" + player.getName().toUpperCase() + "'S COIN RESERVE →\u001b[0m");
                }
                for (counter = 1; counter <= player.getCoins(); ++counter) {
                    formatter.format("\u001b[1;92m $\u001b[0m");
                }
                formatter.format("\n");
                ++curRow;
            }
        }

        /**
        moveCursor = "\u001b[1;181H";
        curRow = 2;
        if (playersOrder.size() == 4) {
            counter = 1;
            //formatter.format(moveCursor + "\u001b[97mTEAMS ↓\u001b[0m\n");
            for (Team team : teams) {
                moveCursor = "\u001b[" + curRow + ";181H";
                formatter.format(moveCursor + "\u001b[97m%1$d°TEAM →  MEMBERS:\u001b[0m", counter);
                for (Player player : team.getPlayers()) {
                    formatter.format("\u001b[97m %1$s\u001b[0m", player.getName().toUpperCase());
                }
                formatter.format("\u001b[97m  TOWERS:\u001b[0m");
                for (Tower tower : team.getTowers()) {
                    if (tower.getColor().equals(TowerColor.BLACK)) {
                        formatter.format("\u001b[1;90m █\u001b[0m");
                    } else if (tower.getColor().equals(TowerColor.WHITE)) {
                        formatter.format("\u001b[1;97m █\u001b[0m");
                    } else {
                        formatter.format("\u001b[38;5;247m █\u001b[0m");
                    }
                }
                formatter.format("\n");
                ++counter;
                ++curRow;
            }
        } else {
            //formatter.format(moveCursor + "\u001b[97mTOWERS AREA ↓\u001b[0m\n");
            for (Team team : teams) {
                moveCursor = "\u001b[" + curRow + ";181H";
                if (team.getPlayers().get(0).getName().equals(client.getName())) {
                    formatter.format(moveCursor + "\u001b[97mYOUR TOWERS →\u001b[0m");
                } else {
                    formatter.format(moveCursor + "\u001b[97m%1$s'S TOWERS →\u001b[0m", team.getPlayers().get(0).getName().toUpperCase());
                }
                for (Tower tower : team.getTowers()) {
                    if (tower.getColor().equals(TowerColor.BLACK)) {
                        formatter.format("\u001b[1;90m █\u001b[0m");
                    } else if (tower.getColor().equals(TowerColor.WHITE)) {
                        formatter.format("\u001b[1;97m █\u001b[0m");
                    } else {
                        formatter.format("\u001b[38;5;247m █\u001b[0m");
                    }
                }
                formatter.format("\n");
                ++curRow;
            }
        }
         **/

        if (!client.name.equals(currentPlayer)) {
            moveCursor = "\u001b[61;1H";
            formatter.format("\n");
            formatter.format(moveCursor + "\u001b[97mWaiting for %1$s...\u001b[0m", currentPlayer.toUpperCase());
            formatter.format("\n");
        }

        try {
            new ProcessBuilder("cmd", "/c", "echo " + formatter).inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        formatter.close();
    }
}

package it.polimi.ingsw.client;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.StudentCharacter;
import it.polimi.ingsw.model.character.impl.Character5;
import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import java.io.Console;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

public class ViewCLI {
    private ClientCLI client;
    private List<Assistant> assistants;
    private List<Island> islands;
    private List<Player> playersOrder;
    private int posMotherNature;
    private List<Cloud> clouds;
    private List<Professor> professors;
    private int coinReserve;
    private List<Character> characters;
    private boolean expert;

    public ViewCLI(ClientCLI client) {
        this.client = client;
    }

    public void handleUpdateView(UpdateViewMessage message) {
        assistants = message.getAssistants();
        islands = message.getIslands();
        playersOrder = message.getPlayersOrder();
        posMotherNature = message.getPosMotherNature();
        clouds = message.getClouds();
        professors = message.getProfessors();
        coinReserve = message.getCoinReserve();
        characters = message.getCharacters();
        expert = message.isExpert();

        ClientCLI.clrscr();
        print();
    }

    //To call when the game starts
    public void printTitle() {
        String eryantis = "░░░░░░░░░░ ░░░░░░░    ░░░   ░░░░   ░░░░░    ░░░  ░░░░░ ░░░░░░░░░░  ░░░░░░░░     ░░░ ░░  \n" +
                "░░░░░░░░░░ ░░░░░░░░   ░░░   ░░░░   ░░░░░    ░░░░ ░░░░░ ░░░░░░░░░░  ░░░░░░░░    ░░░░░░░  \n" +
                " ░░     ░░  ░░   ░░░   ░░    ░░      ░░░     ░░░   ░░  ░░  ░░  ░░     ░░      ░░    ░░  \n" +
                " ░░  ░░ ░░  ░░    ░░    ░░  ░░      ░░ ░░    ░░░░  ░░  ░░  ░░  ░░     ░░      ░░    ░░  \n" +
                " ░░░░░░     ░░   ░░░     ░░░░       ░░ ░░    ░░ ░  ░░  ░░  ░░  ░░     ░░      ░░░░      \n" +
                " ░░░░░░     ░░░░░░░      ░░░░      ░░   ░░   ░░ ░░ ░░      ░░         ░░       ░░░░░░   \n" +
                " ░░  ░░     ░░░░░░        ░░       ░░░░░░░   ░░  ░░░░      ░░         ░░          ░░░░  \n" +
                " ░░     ░░  ░░  ░░░       ░░       ░░░░░░░   ░░  ░░░░      ░░         ░░      ░░    ░░  \n" +
                " ░░     ░░  ░░   ░░░      ░░      ░░     ░░  ░░   ░░░      ░░         ░░      ░░    ░░  \n" +
                "░░░░░░░░░░ ░░░░░  ░░░   ░░░░░░   ░░░░   ░░░░░░░░░ ░░░    ░░░░░░    ░░░░░░░░   ░░░░░░░   \n" +
                "░░░░░░░░░░ ░░░░░   ░░   ░░░░░░   ░░░░   ░░░░░░░░░  ░░    ░░░░░░    ░░░░░░░░   ░░ ░░░    \n";
        System.out.println(eryantis);
    }

    //Print the description of the elements of the game interface
    public void printDescription() {
        String description = "To write";
        System.out.println(description);
    }

    //Arrangement to see
    public void print() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        int counter;

        //Student(●)|Tower(█)|Professor(■)|MotherNature(Ϻ/♦)|NoEntry(Ꭓ)
        //Print islands
        counter = 1;
        for (Island island : islands) {
            formatter.format(counter + "°ISLAND →");
            for (Student student : island.getStudents()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●");
                } else {
                    formatter.format("\u001b[1;95m ●");
                }
            }
            for (Tower tower : island.getTowers()) {
                if (tower.getColor().equals(TowerColor.BLACK)) {
                    formatter.format("\u001b[1;90m █");
                } else if (tower.getColor().equals(TowerColor.WHITE)) {
                    formatter.format("\u001b[1;97m █");
                } else {
                    formatter.format("\u001b[38;5;247m █");
                }
            }
            if (posMotherNature == counter - 1) {
                formatter.format("\u001b[38;5;208 Ϻ"); //♦?
            }
            if ((expert == true) && (island.getNoEntry() > 0)) {
                formatter.format("\u001b[1;91m Ꭓ\n");
            } else {
                formatter.format("\n");
            }
            ++counter;
        }

        //Print clouds
        counter = 1;
        for (Cloud cloud : clouds) {
            formatter.format(counter + "°CLOUD →");
            for (Student student : cloud.getStudents()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●");
                } else {
                    formatter.format("\u001b[1;95m ●");
                }
            }
            formatter.format("\n");
            ++counter;
        }

        //Print characters
        for (Character character : characters) {
            formatter.format("%1$d°CHARACTER →  COST: %2$d", character.getId(), character.getCost());
            if (character instanceof StudentCharacter) {
                formatter.format("  STUDENTS:");
                for (Student student : ((StudentCharacter) character).getStudents()) {
                    if (student.getColor().equals(PawnColor.RED)) {
                        formatter.format("\u001b[1;91m ●");
                    } else if (student.getColor().equals(PawnColor.YELLOW)) {
                        formatter.format("\u001b[1;93m ●");
                    } else if (student.getColor().equals(PawnColor.GREEN)) {
                        formatter.format("\u001b[1;92m ●");
                    } else if (student.getColor().equals(PawnColor.BLUE)) {
                        formatter.format("\u001b[1;94m ●");
                    } else {
                        formatter.format("\u001b[1;95m ●");
                    }
                }
            }
            if (character instanceof Character5) {
                formatter.format("  NO ENTRY:");
                for (counter = 1; counter <= ((Character5) character).getNoEntry(); ++counter) {
                    formatter.format("\u001b[1;91m Ꭓ");
                }
            }
            formatter.format("\n");
        }

        //Print assistants
        counter = 1;
        for (Assistant assistant : assistants) {
            formatter.format("%1$d°ASSISTANT →  Value: %2$d  Moves: %3$d\n", counter, assistant.getValue(), assistant.getMoves());
            ++counter;
        }

        //Print players' schools
        for (Player player : playersOrder) {
            formatter.format(player.getName().toUpperCase() + "'S SCHOOL ↓\n");
            formatter.format("ENTRANCE →");
            for (Student student : player.getSchool().getEntrance()) {
                if (student.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ●");
                } else if (student.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ●");
                } else if (student.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ●");
                } else if (student.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ●");
                } else {
                    formatter.format("\u001b[1;95m ●");
                }
            }
            formatter.format("\n");
            for (PawnColor pawncolor : player.getSchool().getTables().keySet()) {
                formatter.format(pawncolor.toString().toUpperCase() + " TABLE →");
                for (Student student : player.getSchool().getTables().get(pawncolor)) {
                    if (pawncolor.equals(PawnColor.RED)) {
                        formatter.format("\u001b[1;91m ●");
                    } else if (pawncolor.equals(PawnColor.YELLOW)) {
                        formatter.format("\u001b[1;93m ●");
                    } else if (pawncolor.equals(PawnColor.GREEN)) {
                        formatter.format("\u001b[1;92m ●");
                    } else if (pawncolor.equals(PawnColor.BLUE)) {
                        formatter.format("\u001b[1;94m ●");
                    } else {
                        formatter.format("\u001b[1;95m ●");
                    }
                }
                formatter.format("\n");
            }
            formatter.format("PROFESSORS →");
            for (Professor professor : player.getSchool().getProfessors()) {
                if (professor.getColor().equals(PawnColor.RED)) {
                    formatter.format("\u001b[1;91m ■");
                } else if (professor.getColor().equals(PawnColor.YELLOW)) {
                    formatter.format("\u001b[1;93m ■");
                } else if (professor.getColor().equals(PawnColor.GREEN)) {
                    formatter.format("\u001b[1;92m ■");
                } else if (professor.getColor().equals(PawnColor.BLUE)) {
                    formatter.format("\u001b[1;94m ■");
                } else {
                    formatter.format("\u001b[1;95m ■");
                }
            }
            formatter.format("\n");
        }

        //Print players' coin reserve
        if (expert == true) {
            for (Player player : playersOrder) {
                formatter.format(player.getName().toUpperCase() + "'S COIN RESERVE →");
                for (counter = 1; counter <= player.getCoins(); ++counter) {
                    formatter.format("\u001b[1;92m $");
                }
                formatter.format("\n");
            }
        }

        //Print towers
        //TODO

        System.out.println(formatter.toString());
        formatter.close();
    }

    public void printTab() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);

        //Print assistants
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        formatter.format("*                        ASSISTANTS                         *\n");
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        formatter.format("*      NUMBER       *       VALUE       *       MOVES       *\n");
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        int counter = 0;
        for (Assistant assistant : assistants) {
            if (counter >= 10) {
                formatter.format("*        # %1$d       ", counter);
            } else {
                formatter.format("*        # %1$d        ", counter);
            }
            if (assistant.getValue() >= 10) {
                formatter.format("*         %1$d        ", assistant.getValue());
            } else {
                formatter.format("*         %1$d         ", assistant.getValue());
            }
            formatter.format("*         %1$d         *\n", assistant.getMoves());
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            ++counter;
        }
        System.out.println(formatter.toString());
        formatter.close();

        //Print islands
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        formatter.format("*                                                                ISLANDS                                                                *\n");
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        formatter.format("*    NUMBER     *   TOWERS NUMBER   *    TOWERS COLOR   *    RED PAWN   *  YELLOW PAWN  *  GREEN PAWN   *   BLUE PAWN   *   PINK PAWN   *\n");
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        counter = 0;
        for (Island island : islands) {
            if (counter >= 10) {
                formatter.format("*     # %1$d      ", counter);
            } else {
                formatter.format("*      # %1$d      ", counter);
            }
            if (island.getTowers() == null) {
                formatter.format("*         0         *       NONE        ");
            } else {
                if (island.getTowers().get(0).getColor().equals(TowerColor.GRAY)) {
                    formatter.format("*         %1$d         *       %2$s        ", island.getTowers().size(), island.getTowers().get(0).getColor().toString());
                } else {
                    formatter.format("*         %1$d         *       %2$s       ", island.getTowers().size(), island.getTowers().get(0).getColor().toString());
                }
            }
            if (island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.RED)).toList().size() >= 10) {
                formatter.format("*       %1$d      ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.RED)).toList().size());
            } else {
                formatter.format("*       %1$d       ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.RED)).toList().size());
            }
            if (island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.YELLOW)).toList().size() >= 10) {
                formatter.format("*       %1$d      ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.YELLOW)).toList().size());
            } else {
                formatter.format("*       %1$d       ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.YELLOW)).toList().size());
            }
            if (island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.GREEN)).toList().size() >= 10) {
                formatter.format("*       %1$d      ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.GREEN)).toList().size());
            } else {
                formatter.format("*       %1$d       ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.GREEN)).toList().size());
            }
            if (island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.BLUE)).toList().size() >= 10) {
                formatter.format("*       %1$d      ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.BLUE)).toList().size());
            } else {
                formatter.format("*       %1$d       ", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.BLUE)).toList().size());
            }
            if (island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.PINK)).toList().size() >= 10) {
                formatter.format("*       %1$d      *\n", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.PINK)).toList().size());
            } else {
                formatter.format("*       %1$d       *\n", island.getStudents().stream().filter(entry -> entry.getColor().equals(PawnColor.PINK)).toList().size());
            }
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            ++counter;
        }
        System.out.println(formatter.toString());
        formatter.close();

        //Print players' schools
        for (Player player : playersOrder) {
            formatter.format("PLAYER: %1$s\n", player.getName());
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            formatter.format("*                                                                    SCHOOL                                                                                     *\n");
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            formatter.format("*                                    ENTRANCE                                   *                                  DINING ROOM                                  *\n");
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            formatter.format("*   RED PAWN    *  YELLOW PAWN  *  GREEN PAWN   *   BLUE PAWN   *   PINK PAWN   *   RED TABLE   *  YELLOW TABLE *  GREEN TABLE  *  BLUE TABLE   *   PINK TABLE  *\n");
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            formatter.format("*       %1$d       ", player.getSchool().getEntrance().stream().filter(entry -> entry.getColor().equals(PawnColor.RED)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getEntrance().stream().filter(entry -> entry.getColor().equals(PawnColor.YELLOW)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getEntrance().stream().filter(entry -> entry.getColor().equals(PawnColor.GREEN)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getEntrance().stream().filter(entry -> entry.getColor().equals(PawnColor.BLUE)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getEntrance().stream().filter(entry -> entry.getColor().equals(PawnColor.PINK)).toList().size());
            if (player.getSchool().getTableCount(PawnColor.RED) > 9) {
                formatter.format("*       %1$d      ", player.getSchool().getTableCount(PawnColor.RED));
            } else {
                formatter.format("*       %1$d       ", player.getSchool().getTableCount(PawnColor.RED));
            }
            if (player.getSchool().getTableCount(PawnColor.YELLOW) > 9) {
                formatter.format("*       %1$d      ", player.getSchool().getTableCount(PawnColor.YELLOW));
            } else {
                formatter.format("*       %1$d       ", player.getSchool().getTableCount(PawnColor.YELLOW));
            }
            if (player.getSchool().getTableCount(PawnColor.GREEN) > 9) {
                formatter.format("*       %1$d      ", player.getSchool().getTableCount(PawnColor.GREEN));
            } else {
                formatter.format("*       %1$d       ", player.getSchool().getTableCount(PawnColor.GREEN));
            }
            if (player.getSchool().getTableCount(PawnColor.BLUE) > 9) {
                formatter.format("*       %1$d      ", player.getSchool().getTableCount(PawnColor.BLUE));
            } else {
                formatter.format("*       %1$d       ", player.getSchool().getTableCount(PawnColor.BLUE));
            }
            if (player.getSchool().getTableCount(PawnColor.PINK) > 9) {
                formatter.format("*       %1$d      *\n", player.getSchool().getTableCount(PawnColor.PINK));
            } else {
                formatter.format("*       %1$d       *\n", player.getSchool().getTableCount(PawnColor.PINK));
            }
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            formatter.format("*                                    TOWERS                                     *   RED PROF.   *  YELLOW PROF. *  GREEN PROF.  *   BLUE PROF.  *   PINK PROF.  *\n");
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            //formatter.format("*                                       %1$d                                       ", TODO);
            formatter.format("*       %1$d       ", player.getSchool().getProfessors().stream().filter(entry -> entry.getColor().equals(PawnColor.RED)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getProfessors().stream().filter(entry -> entry.getColor().equals(PawnColor.YELLOW)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getProfessors().stream().filter(entry -> entry.getColor().equals(PawnColor.GREEN)).toList().size());
            formatter.format("*       %1$d       ", player.getSchool().getProfessors().stream().filter(entry -> entry.getColor().equals(PawnColor.BLUE)).toList().size());
            formatter.format("*       %1$d       *\n", player.getSchool().getProfessors().stream().filter(entry -> entry.getColor().equals(PawnColor.PINK)).toList().size());
            formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
            System.out.println(formatter.toString());
            formatter.close();
        }

        //Print mother nature position
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        if (posMotherNature > 9) {
            formatter.format("*                  MOTHER NATURE POSITION                   *         %1$d        *\n", posMotherNature);
        } else {
            formatter.format("*                  MOTHER NATURE POSITION                   *         %1$d         *\n", posMotherNature);
        }
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        System.out.println(formatter.toString());
        formatter.close();

        //Print coin reserve
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        formatter.format("*                      COIN RESERVE                     *          %1$d         *\n", coinReserve);
        formatter.format("* - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *\n");
        System.out.println(formatter.toString());
        formatter.close();
    }

    public Player getPlayerFromName(String name){
        for (Player player : playersOrder) {
            if (player.getName().equals(name))
                return player;
        }
        throw new IllegalArgumentException();
    }

    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Island> getIslands() {
        return islands;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    public int getPosMotherNature() {
        return posMotherNature;
    }

    public List<Cloud> getClouds() {
        return clouds;
    }

    public List<Professor> getProfessors() {
        return professors;
    }

    public int getCoinReserve() {
        return coinReserve;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public boolean isExpert() {
        return expert;
    }
}

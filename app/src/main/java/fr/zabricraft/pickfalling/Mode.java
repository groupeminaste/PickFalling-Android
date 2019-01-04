package fr.zabricraft.pickfalling;

public enum Mode {

    CLASSIC(0, R.string.leaderboard_highest_score_in_classic),
    UNICORN(250, R.string.leaderboard_highest_score_in_unicorn),
    FISH(500, R.string.leaderboard_highest_score_in_fish),
    CAT(1000, R.string.leaderboard_highest_score_in_cat),
    BIRD(2500, R.string.leaderboard_highest_score_in_bird),
    SHELL(5000, R.string.leaderboard_highest_score_in_shell),
    QUICK(250, R.string.leaderboard_highest_score_in_quick),
    MIRROR(500, R.string.leaderboard_highest_score_in_mirror),
    ZIGZAG(1000, R.string.leaderboard_highest_score_in_zigzag),
    RANDOM(2500, R.string.leaderboard_highest_score_in_random),
    BONUS(5000, R.string.leaderboard_highest_score_in_bonus),
    EVERYTHING(10000, R.string.leaderboard_highest_score_in_everything),
    CONTROL(15000, R.string.leaderboard_highest_score_in_control),
    RANDOM2(20000, R.string.leaderboard_highest_score_in_random2);

    private int score;
    private int leaderboard;

    Mode(int score, int leaderboard){
        this.score = score;
        this.leaderboard = leaderboard;
    }

    public int getScore(){
        return score;
    }

    public int getLeaderboard(){
        return leaderboard;
    }

}

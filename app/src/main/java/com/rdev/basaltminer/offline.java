package com.rdev.basaltminer;

/*
mine/reward - done
world/select - done
ext/auth - done
upgrade/income - done
upgrade/level - done
upgrade/levelup - done
upgrade/bibaup - done
upgrade/statadd - done
upgrade/statdis - done
upgrade/faq - done
world/list - done
top/list - done
upgrade/list - done
upgrade/streamerup -
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class offline {
    private AppCompatActivity ctx;
    private SharedPreferences playerData;
    private SharedPreferences.Editor playerDataEditor;
    HashMap<Integer, int[]> blocks = new HashMap<Integer, int[]>() {{
        put(1, new int[]{1, 2});
        put(2, new int[]{4, 5, 6});
        put(3, new int[]{24, 9});
        put(4, new int[]{11, 12, 13, 14});
        put(5, new int[]{15, 16, 17});
        put(6, new int[]{20, 21, 23, 24});
        put(7, new int[]{25, 26, 27});
        put(8, new int[]{46, 47, 48, 59});
        put(9, new int[]{28, 29, 30, 30});
        put(10, new int[]{50, 51});
        put(11, new int[]{32, 33, 35});
        put(12, new int[]{35, 36, 37});
        put(13, new int[]{38, 40, 53, 54});
        put(14, new int[]{39, 41, 62});
        put(15, new int[]{42, 32});
        put(16, new int[]{44, 55, 56});
        put(17, new int[]{58});
    }};
    HashMap<Integer, Integer> bibaPercentage = new HashMap<Integer, Integer>() {{
        put(1, 100);
        put(2, 98);
        put(3, 96);
        put(4, 94);
        put(5, 92);
        put(6, 90);
        put(7, 88);
        put(8, 86);
        put(9, 84);
        put(10, 82);
        put(11, 80);
        put(12, 75);
        put(13, 70);
        put(14, 65);
        put(15, 60);
        put(16, 55);
        put(17, 50);
        put(18, 45);
        put(19, 40);
        put(20, 35);
        put(21, 30);
        put(22, 30);
        put(23, 30);
        put(24, 30);
        put(25, 25);
    }};
    ArrayList<World> worlds = new ArrayList<World>() {{
        add(new World("Земля", "5D8052", 1));
        add(new World("Пустыня", "E5B017", 3));
        add(new World("Джунгли", "17FF00", 5));
        add(new World("Терракота", "C26B0E", 8));
        add(new World("Лес", "539D08", 13));
        add(new World("Деревня", "B0B802", 18));
        add(new World("Шахта", "C1C1C1", 26));
        add(new World("Океан", "0041CD", 34));
        add(new World("Глубинная шахта", "666666", 18));
        add(new World("Долина песка душ", "463425", 45));
        add(new World("Адский лес", "832603", 50));
        add(new World("Ад", "F24200", 57));
        add(new World("Бастион", "34302F", 64));
        add(new World("Базальтовые дельты", "533C36", 72));
        add(new World("Обсидиан", "2E0D34", 81));
        add(new World("Энд", "AC08CA", 90));
        add(new World("Бедрок", "000000", 100));
    }};

    @SuppressLint("CommitPrefEdits")
    public offline(AppCompatActivity ctx) {
        this.ctx = ctx;
        playerData = ctx.getSharedPreferences("offlineData", Context.MODE_PRIVATE);
        playerDataEditor = playerData.edit();
    }

    public String processOfflineModeRequest(String path) throws JSONException {
        if (path.startsWith("mine/reward"))
            return mine_reward();
        else if (path.startsWith("ext/auth"))
            return ext_auth();
        else if (path.startsWith("upgrade/income"))
            return upgrade_income();
        else if (path.startsWith("upgrade/levelup"))
            return upgrade_levelup();
        else if (path.startsWith("upgrade/level"))
            return upgrade_level();
        else if (path.startsWith("world/list"))
            return world_list();
        else if (path.startsWith("upgrade/list"))
            return upgrade_list();
        else if (path.startsWith("top/list"))
            return top_list();
        else if (path.startsWith("upgrade/faq"))
            return upgrade_faq();
        else if (path.startsWith("world/select"))
            return world_select(path);
        else if (path.startsWith("upgrade/bibaup"))
            return upgrade_bibaup();
        else if (path.startsWith("upgrade/statadd"))
            return upgrade_statadd(path);
        else if (path.startsWith("upgrade/statdis"))
            return upgrade_statdis();
        else if (path.startsWith("upgrade/streamerup"))
            return upgrade_streamerup(path);
        return "";
    }

    private int getBlock() {
        int[] blocks = this.blocks.get(playerData.getInt("world", 1));
        return blocks[new Random().nextInt(blocks.length)];
    }

    private int getBlockBreakTime() {
        int block = playerData.getInt("block", 1);
        if (block == 58)
            return 86400 * 14 * 1000; // 1 week YEP
        int level = playerData.getInt("level", 1);
        return 1500 + playerData.getInt("world", 1) * 400 - level * 100;
    }

    private String formatNumber(double num) {
        if (num < Math.pow(10, 3))
            return new DecimalFormat("#.#").format(num).replace(",", ".");
        else if (Math.pow(10, 6) > num && num >= Math.pow(10, 3))
            return Math.round(num * 10 / Math.pow(10, 3)) / 10 + "K";
        else if (Math.pow(10, 9) > num && num >= Math.pow(10, 6))
            return Math.round(num * 10 / Math.pow(10, 6)) / 10 + "M";
        else if (Math.pow(10, 12) > num && num >= Math.pow(10, 9))
            return Math.round(num * 10 / Math.pow(10, 9)) / 10 + "B";
        else if (Math.pow(10, 15) > num && num >= Math.pow(10, 12))
            return Math.round(num * 10 / Math.pow(10, 12)) / 10 + "T";
        else if (Math.pow(10, 18) > num && num >= Math.pow(10, 15))
            return Math.round(num * 10 / Math.pow(10, 15)) / 10 + "Q";
        else if (Math.pow(10, 21) > num && num >= Math.pow(10, 18))
            return Math.round(num * 10 / Math.pow(10, 18)) / 10 + "E";
        else if (Math.pow(10, 24) > num && num >= Math.pow(10, 21))
            return Math.round(num * 10 / Math.pow(10, 21)) / 10 + "Z";
        else if (Math.pow(10, 27) > num && num >= Math.pow(10, 24))
            return Math.round(num * 10 / Math.pow(10, 24)) / 10 + "Y";
        else if (Math.pow(10, 30) > num && num >= Math.pow(10, 27))
            return Math.round(num * 10 / Math.pow(10, 27)) / 10 + "O";
        else if (num >= Math.pow(10, 30))
            return "Много";
        else
            return "???";
    }

    private long getLevelPrice() {
        long n = 10;
        int cl = playerData.getInt("level", 1) - 1;
        for (int l = 0; l < cl; l++) {
            if (l % 5 != 0)
                n *= 2;
            else
                n *= 1.5;
        }
        return n;
    }

    private int getBibaPercentage() {
        if (playerData.getInt("biba", 0) > 25)
            return 25;
        return bibaPercentage.get(playerData.getInt("biba", 0) + 1);
    }

    private long getStreamersIncome(int minutes) throws JSONException {
        Log.d("rdev", minutes + "");
        return new Streamers(playerData.getString("streamers", "[0,0,0,0]")).getTotalIncome() * minutes;
    }

    private long getStreamersIncome() throws JSONException {
        return getStreamersIncome(1);
    }

    private boolean worldAvailable(int id) {
        return id <= 17 && id > 0 && playerData.getInt("level", 1) >= worlds.get(id - 1).min_level;
    }

    private Map<String, String> parseQuery(String url) {
        Map<String, String> query = new HashMap<String, String>();
        String[] pairs = url.split("\\?")[1].split("&");
        for (String pair : pairs) {
            query.put(pair.split("=")[0], pair.split("=")[1]);
        }
        return query;
    }

    private String mine_reward() throws JSONException {
        int block = playerData.getInt("block", 1);
        int bonus = Math.random() <= 0.13 ? 10 : 1;
        float gold = playerData.getFloat("gold", 0);
        float boost = playerData.getFloat("boost", 1);
        gold += block * boost * bonus * 0.1;
        block = getBlock();

        gold += getStreamersIncome((int) ((System.currentTimeMillis() - playerData.getLong("lastStreamersIncome", System.currentTimeMillis())) / 1000 / 60));

        playerDataEditor.putInt("block", block);
        playerDataEditor.putFloat("gold", gold);
        playerDataEditor.putLong("lastStreamersIncome", System.currentTimeMillis());
        playerDataEditor.apply();

        int finalBlock = block;
        float finalGold = gold;
        return new JSONObject() {{
            put("block", finalBlock);
            put("time", getBlockBreakTime());
            put("update", new JSONObject() {{
                put("money", formatNumber(finalGold));
                put("points", 0);
            }});
        }}.toString();
    }

    private String ext_auth() throws JSONException {
        return new JSONObject() {{
            put("level", playerData.getInt("level", 1));
            put("world", playerData.getInt("world", 1));
            put("time", getBlockBreakTime());
            put("count", 0);
            put("block", getBlock());
            put("point", 2);
            put("update", new JSONObject() {{
                put("time", System.currentTimeMillis());
                put("money", formatNumber(playerData.getFloat("gold", 0)));
                put("points", 0);
            }});
        }}.toString();
    }

    private String upgrade_income() throws JSONException {
        JSONObject json = new JSONObject() {{
            put("boost", new JSONArray() {{
                put(new JSONArray() {{
                    put("Постоянный множитель");
                    put(formatNumber(playerData.getFloat("boost", 1)));
                }});
            }});
            put("total", formatNumber(playerData.getFloat("boost", 1)));
            put("income", formatNumber(getStreamersIncome()));
        }};
        return json.toString();
    }

    private String upgrade_level() throws JSONException {
        return new JSONObject() {{
            put("level", new JSONArray() {{
                put(playerData.getInt("level", 1));
                put(formatNumber(getLevelPrice()));
            }});
            put("biba", new JSONArray() {{
                put(playerData.getInt("biba", 0));
                put(0);
                put(0);
                put(getBibaPercentage());
            }});
            put("stats", new JSONArray() {{
                put(new JSONArray() {{
                    put("Сила");
                    put(0);
                    put(playerData.getInt("stat_strength", 0));
                }});
                put(new JSONArray() {{
                    put("Ловкость");
                    put(0);
                    put(playerData.getInt("stat_dexterity", 0));
                }});
                put(new JSONArray() {{
                    put("Интеллект");
                    put(0);
                    put(playerData.getInt("stat_intellect", 0));
                }});
            }});
            put("discost", 0);
            put("statpoints", playerData.getInt("statpoints", 0));
        }}.toString();
    }

    private String world_list() {
        int level = playerData.getInt("level", 1);
        JSONArray wrlds = new JSONArray();
        for (World w : this.worlds) {
            if (w.min_level <= level) {
                wrlds.put(new JSONArray() {{
                    put(worlds.indexOf(w));
                    put(w.name);
                    put(w.color);
                }});
            } else {
                wrlds.put(w.min_level);
            }
        }
        return wrlds.toString();
    }

    private String upgrade_list() throws JSONException {
        return new Streamers(playerData.getString("streamers", "[0,0,0,0]")).toJSONArray().toString();
    }

    private String top_list() throws JSONException {
        return new JSONObject() {{
            put("top", new JSONArray() {{
                put(new JSONArray() {{
                    put(new JSONArray() {{
                        put("yep");
                        put(Math.round(playerData.getFloat("gold", 0)));
                    }});
                }});
                put(new JSONArray() {{
                    put(new JSONArray() {{
                        put("yep");
                        put(playerData.getInt("level", 1));
                    }});
                }});
                put(new JSONArray() {{
                    put(new JSONArray() {{
                        put("yep");
                        put(0);
                    }});
                }});
                put(new JSONArray() {{
                    put(new JSONArray() {{
                        put("yep");
                        put(0);
                    }});
                }});
                put(new JSONArray() {{
                    put(new JSONArray() {{
                        put("yep");
                        put(playerData.getFloat("biba", 0));
                    }});
                }});
            }});
            put("time", System.currentTimeMillis());
            put("next", 60);
        }}.toString();
    }

    private String upgrade_faq() {
        return new JSONArray() {{
            put(new JSONArray() {{
                put("");
                put("Нет доступных FAQ");
            }});
        }}.toString();
    }

    private String upgrade_levelup() throws JSONException {
        int level = playerData.getInt("level", 1);
        float gold = playerData.getFloat("gold", 0);
        int statPoints = playerData.getInt("statpoints", 0);
        if (gold < getLevelPrice())
            return new JSONObject() {{
                put("code", 2);
            }}.toString();
        gold -= getLevelPrice();
        level++;
        playerDataEditor.putFloat("gold", gold);
        playerDataEditor.putInt("level", level);
        playerDataEditor.putInt("statpoints", statPoints + 1);
        playerDataEditor.apply();
        int breakTime = getBlockBreakTime();
        float finalGold = gold;
        int finalLevel = level;
        return new JSONObject() {{
            put("code", 1);
            put("cost", new JSONArray() {{
                put(finalLevel);
                put(getLevelPrice());
            }});
            put("time", breakTime);
            put("update", new JSONObject() {{
                put("time", System.currentTimeMillis());
                put("money", formatNumber(finalGold));
                put("points", 0);
            }});
        }}.toString();
    }

    private String world_select(String path) throws JSONException {
        Map<String, String> q = parseQuery(path);
        if (!q.containsKey("id"))
            return "";
        int world_id = Integer.parseInt(q.get("id"));
        if (!worldAvailable(world_id))
            return "";
        playerDataEditor.putInt("world", world_id);
        playerDataEditor.apply();
        int block = getBlock();
        int breakTime = getBlockBreakTime();
        playerDataEditor.putInt("block", block);
        playerDataEditor.apply();
        return new JSONObject() {{
            put("point", 2);
            put("time", getBlockBreakTime());
            put("block", block);
        }}.toString();
    }

    private String upgrade_bibaup() throws JSONException {
        int biba = playerData.getInt("biba", 0);
        float boost = playerData.getFloat("boost", 1);
        if (Math.random() < getBibaPercentage() / 100.0) {
            playerDataEditor.putInt("biba", biba + 1);
            playerDataEditor.putFloat("boost", (float) (boost + 0.1));
        } else {
            playerDataEditor.putInt("biba", biba - 1);
            playerDataEditor.putFloat("boost", (float) (boost - 0.1));
        }
        playerDataEditor.apply();
        return new JSONObject() {{
            put("code", 1);
            put("cost", new JSONArray() {{
                put(biba);
                put(0);
                put(0);
                put(getBibaPercentage());
            }});
            put("update", new JSONObject() {{
                put("time", System.currentTimeMillis());
                put("money", formatNumber(playerData.getFloat("gold", 0)));
                put("points", 0);
            }});
        }}.toString();
    }

    private String upgrade_statadd(String path) throws JSONException {
        int statpoints = playerData.getInt("statpoints", 0);
        if (statpoints < 1)
            return new JSONObject() {{
                put("code", 2);
            }}.toString();
        Map<String, String> q = parseQuery(path);
        if (!q.containsKey("id"))
            return "";
        int stat_id = Integer.parseInt(q.get("id"));
        if (stat_id > 3 || stat_id < 1)
            return "";
        switch (stat_id) {
            case 1:
                playerDataEditor.putInt("stat_strength", playerData.getInt("stat_strength", 0) + 1);
                break;
            case 2:
                playerDataEditor.putInt("stat_dexterity", playerData.getInt("stat_dexterity", 0) + 1);
                break;
            case 3:
                playerDataEditor.putInt("stat_intellect", playerData.getInt("stat_intellect", 0) + 1);
                break;
        }
        playerDataEditor.putInt("statpoints", playerData.getInt("statpoints", 0) - 1);
        playerDataEditor.apply();
        return new JSONObject() {{
            put("code", 1);
            put("stats", new JSONArray() {{
                put(new JSONArray() {{
                    put("Сила");
                    put(0);
                    put(playerData.getInt("stat_strength", 0));
                }});
                put(new JSONArray() {{
                    put("Ловкость");
                    put(0);
                    put(playerData.getInt("stat_dexterity", 0));
                }});
                put(new JSONArray() {{
                    put("Интеллект");
                    put(0);
                    put(playerData.getInt("stat_intellect", 0));
                }});
            }});
            put("statpoints", playerData.getInt("statpoints", 0));
        }}.toString();
    }

    private String upgrade_statdis() throws JSONException {
        int statpoints = playerData.getInt("stat_strength", 0) + playerData.getInt("stat_dexterity", 0) + playerData.getInt("stat_intellect", 0);
        if (statpoints == 0)
            return new JSONObject() {{
                put("code", 2);
            }}.toString();
        statpoints += playerData.getInt("statpoints", 0);
        playerDataEditor.putInt("statpoints", statpoints);
        playerDataEditor.putInt("stat_strength", 0);
        playerDataEditor.putInt("stat_dexterity", 0);
        playerDataEditor.putInt("stat_intellect", 0);
        playerDataEditor.apply();
        int finalStatpoints = statpoints;
        return new JSONObject() {{
            put("code", 1);
            put("statpoints", finalStatpoints);
            put("update", new JSONObject() {{
                put("time", System.currentTimeMillis());
                put("money", formatNumber(playerData.getFloat("gold", 0)));
                put("points", 0);
            }});
            put("cost", 0);
        }}.toString();
    }

    private String upgrade_streamerup(String path) throws JSONException {
        Map<String, String> q = parseQuery(path);
        if (!q.containsKey("id"))
            return "";
        int sid = Integer.parseInt(q.get("id"));
        if (sid > 3 || sid < 0)
            return "";
        float gold = playerData.getFloat("gold", 0);
        Streamers streamers = new Streamers(playerData.getString("streamers", "[0,0,0,0]"));
        if (!streamers.canBeUpgraded(sid, (long) gold))
            return new JSONObject() {{
                put("code", 1);
            }}.toString();
        gold -= streamers.getCost(sid);
        streamers.upgrade(sid);
        playerDataEditor.putFloat("gold", gold);
        playerDataEditor.putString("streamers", streamers.exportLevels());
        playerDataEditor.apply();
        return new JSONObject() {{
            put("code", 3);
            put("info", streamers.toJSONArray(sid));
            put("update", new JSONObject() {{
                put("time", System.currentTimeMillis());
                put("money", formatNumber(playerData.getFloat("gold", 0)));
                put("points", 0);
            }});
        }}.toString();
    }

    private class World {
        public String name;
        public String color;
        public int min_level;

        public World(String name, String color, int min_level) {
            this.name = name;
            this.color = color;
            this.min_level = min_level;
        }
    }

    private class Streamers {
        private Streamer[] st = new Streamer[4];

        public Streamers(String arr) throws JSONException {
            JSONArray s = new JSONArray(arr);
            st[0] = new Streamer(0, s.getInt(0), "5opka", 1000000, 1000);
            st[1] = new Streamer(1, s.getInt(1), "JackLooney", 100000000, 10000);
            st[2] = new Streamer(2, s.getInt(2), "exx1dae", 10000000000L, 100000);
            st[3] = new Streamer(3, s.getInt(3), "Zakviel", 10000000000000L, 10000000);
        }

        public boolean canBeUpgraded(int id, long gold) {
            return st[id].canBeUpgraded(gold);
        }

        public void upgrade(int id) {
            st[id].upgrade();
        }

        public JSONArray toJSONArray() {
            JSONArray s = new JSONArray();
            for (Streamer _s : st) {
                s.put(_s.toJSONArray());
            }
            return s;
        }

        public JSONArray toJSONArray(int id) {
            return st[id].toJSONArray();
        }

        public String toString() {
            return toJSONArray().toString();
        }

        public long getTotalIncome() {
            long i = 0;
            for (Streamer s : st) {
                i += s.getIncome();
            }
            return i;
        }

        public long getCost(int id) {
            return st[id].getCost();
        }

        public String exportLevels() {
            JSONArray s = new JSONArray();
            for (Streamer _s : st) {
                s.put(_s.count);
            }
            return s.toString();
        }
    }

    private class Streamer {
        public int id;
        public int count;
        public String name;

        private long def_cost;
        private long def_income;

        public Streamer(int id, int count, String name, long def_cost, long def_income) {
            this.id = id;
            this.count = count;
            this.name = name;
            this.def_cost = def_cost;
            this.def_income = def_income;
        }

        public long getCost() {
            return def_cost * (count + 1);
        }

        private long getIncomeForCount(int c) {
            if (c == 0)
                return 0;
            return (long) (def_income * (Math.pow(1.5, c)));
        }

        public JSONArray toJSONArray() {
            return new JSONArray() {{
                put(id);
                put(count);
                put(name);
                put(formatNumber(getCost()));
                put(0); // redstone
                put(formatNumber(getIncomeForCount(count)));
                put(formatNumber(getIncomeForCount(count + 1)));
            }};
        }

        public boolean canBeUpgraded(long gold) {
            return gold >= getCost();
        }

        public void upgrade() {
            this.count++;
        }

        public long getIncome() {
            return getIncomeForCount(count);
        }
    }
}

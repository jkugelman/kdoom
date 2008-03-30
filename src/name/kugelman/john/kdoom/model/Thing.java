package name.kugelman.john.kdoom.model;

import java.io.*;
import java.util.*;

import name.kugelman.john.kdoom.file.*;

public class Thing {
   public enum Kind {
        PLAYER,
        MONSTER,
        WEAPON,
        AMMO,
        HEALTH,
        ARMOR,
        POWER_UP,
        KEY,
        OBSTACLE,
        DECORATION,
        SPECIAL,
        UNKNOWN
    }

    private static class Details {
        Kind   kind;
        String sprite, sequence;
        short  radius;
        String name;
        
        public Details(Kind kind, String sprite, String sequence, short radius, String name) {
            this.kind     = kind;
            this.sprite   = sprite;
            this.sequence = sequence;
            this.radius   = radius;
            this.name     = name;
        }
    }

    private static Map<Short, Details> typeDetails;
    private static Details             unknownDetails;

    static {
        typeDetails = new HashMap<Short, Details>(); 

        addPlayer    (1,    "PLAY", 1,         16, "Player 1 Start");
        addPlayer    (2,    "PLAY", 2,         16, "Player 2 Start");
        addPlayer    (3,    "PLAY", 3,         16, "Player 3 Start");
        addPlayer    (4,    "PLAY", 4,         16, "Player 4 Start");
        addPlayer    (11,   null,   null,      16, "Deathmatch Start");
                                                   
        addMonster   (3004, "POSS",            16, "Former Human");
        addMonster   (84,   "SSWV",            20, "Wolfenstein SS"); 
        addMonster   (9,    "SPOS",            20, "Former Sargeant"); 
        addMonster   (65,   "CPOS",            20, "Heavy Weapon Dude"); 
        addMonster   (3001, "TROO",            20, "Imp"); 
        addMonster   (3002, "SARG",            30, "Demon"); 
        addMonster   (58,   "SARG",            30, "Spectre"); 
        addMonster   (3006, "SKUL",            16, "Lost Soul"); 
        addMonster   (3005, "HEAD",            31, "Cacodemon"); 
        addMonster   (69,   "BOS2",            24, "Hell Knight"); 
        addMonster   (3003, "BOSS",            24, "Baron of Hell"); 
        addMonster   (68,   "BSPI",            64, "Arachnotron"); 
        addMonster   (71,   "PAIN",            31, "Pain Elemental"); 
        addMonster   (66,   "SKEL",            20, "Revenant"); 
        addMonster   (67,   "FATT",            48, "Mancubus"); 
        addMonster   (64,   "VILE",            20, "Arch-Vile"); 
        addMonster   (7,    "SPID",            128,"Spider Mastermind"); 
        addMonster   (16,   "CYBR",            40, "Cyberdemon"); 
        addMonster   (88,   "BBRN",            16, "Boss Brain");
                                                   
        addWeapon    (2005, "CSAW",  "A",      20, "Chainsaw");
        addWeapon    (2001, "SHOT",  "A",      20, "Shotgun");
        addWeapon    (82,   "SGN2",  "A",      20, "Double-Barreled Shotgun");
        addWeapon    (2002, "MGUN",  "A",      20, "Chaingun");
        addWeapon    (2003, "LAUN",  "A",      20, "Rocket Launcher");
        addWeapon    (2004, "PLAS",  "A",      20, "Plasma Gun");
        addWeapon    (2006, "BFUG",  "A",      20, "BFG 9000");
                                                   
        addAmmo      (2007, "CLIP",  "A",      8,  "Ammo Clip");
        addAmmo      (2008, "SHEL",  "A",      8,  "Shotgun Shells");
        addAmmo      (2010, "ROCK",  "A",      8,  "Rocket");
        addAmmo      (2047, "CELL",  "A",      8,  "Cell Charge");
        addAmmo      (2048, "AMMO",  "A",      16, "Box of Ammo");
        addAmmo      (2049, "SBOX",  "A",      16, "Box of Shells");
        addAmmo      (2046, "BROK",  "A",      16, "Box of Rockets");
        addAmmo      (17,   "CELP",  "A",      16, "Cell Charge Pack");
        addAmmo      (8,    "BPAK",  "A",      16, "Backpack");
                                                   
        addHealth    (2011, "STIM",  "A",      12, "Stimpak +10%");
        addHealth    (2012, "MEDI",  "A",      16, "Medikit +25%");
        addHealth    (2014, "BON1",  "ABCDCB", 8,  "Health Potion +1%");
        addArmor     (2015, "BON2",  "ABCDCB", 8,  "Spirit Armor +1%");
        addArmor     (2018, "ARM1",  "AB",     16, "Green Armor +100%");
        addArmor     (2019, "ARM2",  "AB",     16, "Blue Armor +200%");
        addPowerUp   (83,   "MEGA",  "ABCD",   16, "Megasphere");
        addPowerUp   (2013, "SOUL",  "ABCDCB", 16, "Soulsphere");
        addPowerUp   (2022, "PINV",  "ABCD",   16, "Invulnerability Sphere");
        addPowerUp   (2023, "PSTR",  "A",      16, "Berkserker Pack");
        addPowerUp   (2024, "PINS",  "ABCD",   16, "Invisibility Sphere");
        addPowerUp   (2025, "SUIT",  "A",      16, "Radiation Suit");
        addPowerUp   (2026, "PMAP",  "ABCDCB", 16, "Computer Map");
        addPowerUp   (2045, "PVIS",  "AB",     16, "Light Amplification Goggles");
                                                   
        addKey       (5,    "BKEY",  "AB",     16, "Blue Key");
        addKey       (40,   "BSKU",  "AB",     16, "Blue Skullkey");
        addKey       (13,   "RKEY",  "AB",     16, "Red Key");
        addKey       (38,   "RSKU",  "AB",     16, "Red Skullkey");
        addKey       (6,    "YKEY",  "AB",     16, "Yellow Key");
        addKey       (39,   "YSKU",  "AB",     16, "Yellow Skullkey");
                                                   
        addObstacle  (2035, "BAR1",  "AB",     10, "Barrel");
        addObstacle  (72,   "KEEN",  "A",      16, "Commander Keen"); 
                                                   
        addObstacle  (48,   "ELEC",  "A",      16, "Tall Techno Pillar");
        addObstacle  (30,   "COL1",  "A",      16, "Tall Green Pillar");
        addObstacle  (32,   "COL3",  "A",      16, "Tall Red Pillar");
        addObstacle  (31,   "COL2",  "A",      16, "Short Green Pillar");
        addObstacle  (36,   "COL5",  "AB",     16, "Short Green Pillar with Beating Heart");
        addObstacle  (33,   "COL4",  "A",      16, "Short Red Pillar");
        addObstacle  (37,   "COL6",  "A",      16, "Short Red Pillar with Skull");
        addObstacle  (47,   "SMIT",  "A",      16, "Stalagmite");
        addObstacle  (43,   "TRE1",  "A",      16, "Burnt Tree");
        addObstacle  (54,   "TRE2",  "A",      32, "Large Brown Tree");
                                                   
        addObstacle  (2028, "COLU",  "A",      16, "Floor Lamp");
        addObstacle  (85,   "TLMP",  "ABCD",   16, "Tall Techno Floor Lamp");
        addObstacle  (86,   "TLP2",  "ABCD",   16, "Short Techno Floor Lamp");
        addObstacle  (34,   "CAND",  "A",      16, "Candle");
        addObstacle  (35,   "CBRA",  "A",      16, "Candelabra");
        addObstacle  (44,   "TBLU",  "ABCD",   16, "Tall Blue Firestick");
        addObstacle  (45,   "TGRE",  "ABCD",   16, "Tall Green Firestick");
        addObstacle  (46,   "TRED",  "ABCD",   16, "Tall Red Firestick");
        addObstacle  (55,   "SMBT",  "ABCD",   16, "Short Blue Firestick");
        addObstacle  (56,   "SMGT",  "ABCD",   16, "Short Green Firestick");
        addObstacle  (57,   "SMRT",  "ABCD",   16, "Short Red Firestick");
        addObstacle  (70,   "FCAN",  "ABC",    16, "Burning Barrel");
                                                   
        addObstacle  (41,   "CEYE",  "ABCB",   16, "Evil Eye");
        addObstacle  (42,   "FSKU",  "ABC",    16, "Floating Skull");
                                                   
        addObstacle  (49,   "GOR1",  "ABCB",   16, "Hanging Body, Twitching");
        addDecoration(63,   "GOR1",  "ABCB",   16, "Hanging Body, Twitching");
        addObstacle  (50,   "GOR2",  "A",      16, "Hanging Body, Arms Out");
        addDecoration(59,   "GOR2",  "A",      16, "Hanging Body, Arms Out");
        addObstacle  (51,   "GOR3",  "A",      16, "Hanging Victim, One-Legged");
        addDecoration(61,   "GOR3",  "A",      16, "Hanging Victim, One-Legged");
        addObstacle  (52,   "GOR4",  "A",      16, "Hanging Pair of Legs");
        addDecoration(60,   "GOR4",  "A",      16, "Hanging Pair of Legs");
        addObstacle  (53,   "GOR5",  "A",      16, "Hanging Leg");
        addDecoration(62,   "GOR5",  "A",      16, "Hanging Leg");
        addObstacle  (73,   "HDB1",  "A",      16, "Hanging Body, Guts Removed");
        addObstacle  (74,   "HDB2",  "A",      16, "Hanging Body, Guts and Brain Removed");
        addObstacle  (76,   "HDB3",  "A",      16, "Hanging Torso, Looking Down");
        addObstacle  (76,   "HDB4",  "A",      16, "Hanging Torso, Open Skull");
        addObstacle  (77,   "HDB5",  "A",      16, "Hanging Torso, Looking Up");
        addObstacle  (78,   "HDB6",  "A",      16, "Hanging Torso, Brain Removed");
                                                   
        addObstacle  (25,   "POL1",  "A",      16, "Impaled Human");
        addObstacle  (26,   "POL1",  "AB",     16, "Twitching Impaled Human");
        addObstacle  (27,   "POL1",  "A",      16, "Skull on a Pole");
        addObstacle  (28,   "POL1",  "A",      16, "Skull Shish Kebob");
        addObstacle  (29,   "POL1",  "AB",     16, "Pile of Skulls and Candles");
                                                   
        addDecoration(10,   "PLAY",  "W",      20, "Bloody Mess");
        addDecoration(12,   "PLAY",  "W",      20, "Bloody Mess");
        addDecoration(24,   "POL5",  "A",      20, "Pool of Blood and Flesh");
        addDecoration(79,   "POB1",  "A",      20, "Pool of Blood");
        addDecoration(80,   "POB2",  "A",      20, "Pool of Blood");
        addDecoration(81,   "BRS1",  "A",      20, "Pool of Brains");
        addDecoration(15,   "PLAY",  "A",      20, "Dead Marine");
        addDecoration(18,   "POSS",  "A",      20, "Dead Former Human");
        addDecoration(19,   "SPOS",  "A",      20, "Dead Former Sargeant");
        addDecoration(20,   "TROO",  "A",      20, "Dead Imp");
        addDecoration(21,   "SARG",  "A",      20, "Dead Demon");
        addDecoration(22,   "HEAD",  "A",      20, "Dead Cacodemon");
        addDecoration(23,   "SKUL",  "A",      20, "Dead Lost Soul");
                                                   
        addSpecial   (14,   null,    null,     20, "Teleport Landing");
        addSpecial   (89,   null,    null,     20, "Boss Shooter");
        addSpecial   (87,   null,    null,     20, "Spawn Spot");

        unknownDetails = new Details(Kind.UNKNOWN, null, null, (short) 20, "Unknown (???)");
    }

    private static void addPlayer(int type, String sprite, Integer number, int radius, String name) {
        addType(type, Kind.PLAYER, sprite, "*", radius, name);
    }

    private static void addMonster(int type, String sprite, int radius, String name) {
        addType(type, Kind.MONSTER, sprite, "*", radius, name);
    }

    private static void addWeapon(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.WEAPON, sprite, sequence, radius, name);
    }

    private static void addAmmo(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.AMMO, sprite, sequence, radius, name);
    }

    private static void addHealth(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.HEALTH, sprite, sequence, radius, name);
    }

    private static void addArmor(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.ARMOR, sprite, sequence, radius, name);
    }

    private static void addPowerUp(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.POWER_UP, sprite, sequence, radius, name);
    }

    private static void addKey(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.KEY, sprite, sequence, radius, name);
    }

    private static void addObstacle(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.OBSTACLE, sprite, sequence, radius, name);
    }

    private static void addDecoration(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.DECORATION, sprite, sequence, radius, name);
    }

    private static void addSpecial(int type, String sprite, String sequence, int radius, String name) {
        addType(type, Kind.SPECIAL, sprite, sequence, radius, name);
    }

    private static void addType(int type, Kind kind, String sprite, String sequence, int radius, String name) {
        typeDetails.put((short) type, new Details(kind, sprite, sequence, (short) radius, name));
    }


    private short    number;
    private Location location;
    private short    angle;
    private short    type;
    private short    flags;
    private Details  details;
    private Sprite   sprite;

    public Thing(Wad wad, short number, Location location, short angle, short type, short flags) throws IOException {
        this.number   = number;
        this.location = location;
        this.angle    = angle;
        this.type     = type;
        this.flags    = flags;
        this.details  = typeDetails.get(type);

        if (details == null) {
            details = unknownDetails;
        }
        
        if (details.sprite != null) {
            this.sprite = new Sprite(wad, details.sprite);
        }
    }


    public short getNumber() {
        return number;
    }

    public Location getLocation() {
        return location;
    }

    public short getAngle() {
        return angle;
    }

    public short getType() {
        return type;
    }

    public short getFlags() {
        return flags;
    }


    public Kind getKind() {
        return details.kind;
    }

    public boolean isDirectional() {
        switch (details.kind) {
            case PLAYER:
            case MONSTER:
            case SPECIAL:
            case UNKNOWN:
                return true;

            default:
                return false;
        }
    }

    public short getRadius() {
        return details.radius;
    }

    public String getTypeName() {
        return details.name;
    }

    public Sprite getSprite() {
        return sprite;
    }
}

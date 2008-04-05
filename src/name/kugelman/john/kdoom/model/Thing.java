package name.kugelman.john.kdoom.model;

import java.awt.image.*;
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
        Kind    kind;
        String  spriteName, frameSequence;
        short   radius;
        boolean isHanging;
        String  name;

        public Details(Kind kind, String spriteName, String frameSequence, short radius, boolean isHanging, String name) {
            this.kind          = kind;
            this.spriteName    = spriteName;
            this.frameSequence = frameSequence;
            this.radius        = radius;
            this.isHanging     = isHanging;
            this.name          = name;
        }
    }

    private static Map<String, Sprite> sprites;
    private static Map<Short, Details> typeDetails;
    private static Details             unknownDetails;

    static {
        sprites     = new HashMap<String, Sprite>();
        typeDetails = new HashMap<Short, Details>();

        addType(1,    Kind.PLAYER,     "PLAY", "A1-D1", 16,  false, "Player 1 Start");
        addType(2,    Kind.PLAYER,     "PLAY", "A1-D1", 16,  false, "Player 2 Start");
        addType(3,    Kind.PLAYER,     "PLAY", "A1-D1", 16,  false, "Player 3 Start");
        addType(4,    Kind.PLAYER,     "PLAY", "A1-D1", 16,  false, "Player 4 Start");
        addType(11,   Kind.PLAYER,     "PLAY", "A1-D1", 16,  false, "Deathmatch Start");

        addType(3004, Kind.MONSTER,    "POSS", "A1-D1", 20,  false, "Former Human");
        addType(9,    Kind.MONSTER,    "SPOS", "A1-D1", 20,  false, "Former Sargeant");
        addType(65,   Kind.MONSTER,    "CPOS", "A1-D1", 20,  false, "Heavy Weapon Dude");
        addType(3001, Kind.MONSTER,    "TROO", "A1-D1", 20,  false, "Imp");
        addType(3002, Kind.MONSTER,    "SARG", "A1-D1", 30,  false, "Demon");
        addType(58,   Kind.MONSTER,    "SARG", "A1-D1", 30,  false, "Spectre");
        addType(3006, Kind.MONSTER,    "SKUL", "A1-D1", 16,  true,  "Lost Soul");
        addType(3005, Kind.MONSTER,    "HEAD", "A1-D1", 31,  true,  "Cacodemon");
        addType(69,   Kind.MONSTER,    "BOS2", "A1-D1", 24,  false, "Hell Knight");
        addType(3003, Kind.MONSTER,    "BOSS", "A1-D1", 24,  false, "Baron of Hell");
        addType(68,   Kind.MONSTER,    "BSPI", "A1-D1", 64,  false, "Arachnotron");
        addType(71,   Kind.MONSTER,    "PAIN", "A1-D1", 31,  true,  "Pain Elemental");
        addType(66,   Kind.MONSTER,    "SKEL", "A1-D1", 20,  false, "Revenant");
        addType(67,   Kind.MONSTER,    "FATT", "A1-D1", 48,  false, "Mancubus");
        addType(64,   Kind.MONSTER,    "VILE", "A1-D1", 20,  false, "Arch-Vile");
        addType(7,    Kind.MONSTER,    "SPID", "A1-D1", 128, false, "Spider Mastermind");
        addType(16,   Kind.MONSTER,    "CYBR", "A1-D1", 40,  false, "Cyberdemon");
        addType(88,   Kind.MONSTER,    "BBRN", "A1-D1", 16,  false, "Boss Brain");
        addType(84,   Kind.MONSTER,    "SSWV", "A1-D1", 20,  false, "Wolfenstein SS");

        addType(2005, Kind.WEAPON,     "CSAW", "A",     20,  false, "Chainsaw");
        addType(2001, Kind.WEAPON,     "SHOT", "A",     20,  false, "Shotgun");
        addType(82,   Kind.WEAPON,     "SGN2", "A",     20,  false, "Double-Barreled Shotgun");
        addType(2002, Kind.WEAPON,     "MGUN", "A",     20,  false, "Chaingun");
        addType(2003, Kind.WEAPON,     "LAUN", "A",     20,  false, "Rocket Launcher");
        addType(2004, Kind.WEAPON,     "PLAS", "A",     20,  false, "Plasma Gun");
        addType(2006, Kind.WEAPON,     "BFUG", "A",     20,  false, "BFG 9000");

        addType(2007, Kind.AMMO,       "CLIP", "A",     8,   false, "Ammo Clip");
        addType(2008, Kind.AMMO,       "SHEL", "A",     8,   false, "Shotgun Shells");
        addType(2010, Kind.AMMO,       "ROCK", "A",     8,   false, "Rocket");
        addType(2047, Kind.AMMO,       "CELL", "A",     8,   false, "Cell Charge");
        addType(2048, Kind.AMMO,       "AMMO", "A",     16,  false, "Box of Ammo");
        addType(2049, Kind.AMMO,       "SBOX", "A",     16,  false, "Box of Shells");
        addType(2046, Kind.AMMO,       "BROK", "A",     16,  false, "Box of Rockets");
        addType(17,   Kind.AMMO,       "CELP", "A",     16,  false, "Cell Charge Pack");
        addType(8,    Kind.AMMO,       "BPAK", "A",     16,  false, "Backpack");

        addType(2011, Kind.HEALTH,     "STIM", "A",     12,  false, "Stimpak +10%");
        addType(2012, Kind.HEALTH,     "MEDI", "A",     16,  false, "Medikit +25%");
        addType(2014, Kind.HEALTH,     "BON1", "A-D-B", 8,   false, "Health Potion +1%");
        addType(2015, Kind.ARMOR,      "BON2", "A-D-B", 8,   false, "Spirit Armor +1%");
        addType(2018, Kind.ARMOR,      "ARM1", "AB",    16,  false, "Green Armor +100%");
        addType(2019, Kind.ARMOR,      "ARM2", "AB",    16,  false, "Blue Armor +200%");
        addType(83,   Kind.POWER_UP,   "MEGA", "A-D",   16,  false, "Megasphere");
        addType(2013, Kind.POWER_UP,   "SOUL", "A-D-B", 16,  false, "Soulsphere");
        addType(2022, Kind.POWER_UP,   "PINV", "A-D",   16,  false, "Invulnerability Sphere");
        addType(2023, Kind.POWER_UP,   "PSTR", "A",     16,  false, "Berkserker Pack");
        addType(2024, Kind.POWER_UP,   "PINS", "A-D",   16,  false, "Invisibility Sphere");
        addType(2025, Kind.POWER_UP,   "SUIT", "A",     16,  false, "Radiation Suit");
        addType(2026, Kind.POWER_UP,   "PMAP", "A-D-B", 16,  false, "Computer Map");
        addType(2045, Kind.POWER_UP,   "PVIS", "AB",    16,  false, "Light Amplification Goggles");

        addType(5,    Kind.KEY,        "BKEY", "AB",    16,  false, "Blue Key");
        addType(40,   Kind.KEY,        "BSKU", "AB",    16,  false, "Blue Skullkey");
        addType(13,   Kind.KEY,        "RKEY", "AB",    16,  false, "Red Key");
        addType(38,   Kind.KEY,        "RSKU", "AB",    16,  false, "Red Skullkey");
        addType(6,    Kind.KEY,        "YKEY", "AB",    16,  false, "Yellow Key");
        addType(39,   Kind.KEY,        "YSKU", "AB",    16,  false, "Yellow Skullkey");

        addType(2035, Kind.OBSTACLE,   "BAR1", "AB",    10,  false, "Barrel");
        addType(72,   Kind.OBSTACLE,   "KEEN", "A",     16,  true,  "Commander Keen");

        addType(48,   Kind.OBSTACLE,   "ELEC", "A",     16,  false, "Tall Techno Pillar");
        addType(30,   Kind.OBSTACLE,   "COL1", "A",     16,  false, "Tall Green Pillar");
        addType(32,   Kind.OBSTACLE,   "COL3", "A",     16,  false, "Tall Red Pillar");
        addType(31,   Kind.OBSTACLE,   "COL2", "A",     16,  false, "Short Green Pillar");
        addType(36,   Kind.OBSTACLE,   "COL5", "AB",    16,  false, "Short Green Pillar with Beating Heart");
        addType(33,   Kind.OBSTACLE,   "COL4", "A",     16,  false, "Short Red Pillar");
        addType(37,   Kind.OBSTACLE,   "COL6", "A",     16,  false, "Short Red Pillar with Skull");
        addType(47,   Kind.OBSTACLE,   "SMIT", "A",     16,  false, "Stalagmite");
        addType(43,   Kind.OBSTACLE,   "TRE1", "A",     16,  false, "Burnt Tree");
        addType(54,   Kind.OBSTACLE,   "TRE2", "A",     32,  false, "Large Brown Tree");

        addType(2028, Kind.OBSTACLE,   "COLU", "A",     16,  false, "Floor Lamp");
        addType(85,   Kind.OBSTACLE,   "TLMP", "A-D",   16,  false, "Tall Techno Floor Lamp");
        addType(86,   Kind.OBSTACLE,   "TLP2", "A-D",   16,  false, "Short Techno Floor Lamp");
        addType(34,   Kind.OBSTACLE,   "CAND", "A",     16,  false, "Candle");
        addType(35,   Kind.OBSTACLE,   "CBRA", "A",     16,  false, "Candelabra");
        addType(44,   Kind.OBSTACLE,   "TBLU", "A-D",   16,  false, "Tall Blue Firestick");
        addType(45,   Kind.OBSTACLE,   "TGRE", "A-D",   16,  false, "Tall Green Firestick");
        addType(46,   Kind.OBSTACLE,   "TRED", "A-D",   16,  false, "Tall Red Firestick");
        addType(55,   Kind.OBSTACLE,   "SMBT", "A-D",   16,  false, "Short Blue Firestick");
        addType(56,   Kind.OBSTACLE,   "SMGT", "A-D",   16,  false, "Short Green Firestick");
        addType(57,   Kind.OBSTACLE,   "SMRT", "A-D",   16,  false, "Short Red Firestick");
        addType(70,   Kind.OBSTACLE,   "FCAN", "ABC",   10,  false, "Burning Barrel");

        addType(41,   Kind.OBSTACLE,   "CEYE", "ABCB",  16,  false, "Evil Eye");
        addType(42,   Kind.OBSTACLE,   "FSKU", "ABC",   16,  false, "Floating Skull");

        addType(49,   Kind.OBSTACLE,   "GOR1", "ABCB",  16,  true,  "Hanging Body, Twitching");
        addType(63,   Kind.DECORATION, "GOR1", "ABCB",  16,  true,  "Hanging Body, Twitching");
        addType(50,   Kind.OBSTACLE,   "GOR2", "A",     16,  true,  "Hanging Body, Arms Out");
        addType(59,   Kind.DECORATION, "GOR2", "A",     16,  true,  "Hanging Body, Arms Out");
        addType(51,   Kind.OBSTACLE,   "GOR3", "A",     16,  true,  "Hanging Victim, One-Legged");
        addType(61,   Kind.DECORATION, "GOR3", "A",     16,  true,  "Hanging Victim, One-Legged");
        addType(52,   Kind.OBSTACLE,   "GOR4", "A",     16,  true,  "Hanging Pair of Legs");
        addType(60,   Kind.DECORATION, "GOR4", "A",     16,  true,  "Hanging Pair of Legs");
        addType(53,   Kind.OBSTACLE,   "GOR5", "A",     16,  true,  "Hanging Leg");
        addType(62,   Kind.DECORATION, "GOR5", "A",     16,  true,  "Hanging Leg");
        addType(73,   Kind.OBSTACLE,   "HDB1", "A",     16,  true,  "Hanging Body, Guts Removed");
        addType(74,   Kind.OBSTACLE,   "HDB2", "A",     16,  true,  "Hanging Body, Guts and Brain Removed");
        addType(76,   Kind.OBSTACLE,   "HDB3", "A",     16,  true,  "Hanging Torso, Looking Down");
        addType(76,   Kind.OBSTACLE,   "HDB4", "A",     16,  true,  "Hanging Torso, Open Skull");
        addType(77,   Kind.OBSTACLE,   "HDB5", "A",     16,  true,  "Hanging Torso, Looking Up");
        addType(78,   Kind.OBSTACLE,   "HDB6", "A",     16,  true,  "Hanging Torso, Brain Removed");

        addType(25,   Kind.OBSTACLE,   "POL1", "A",     16,  false, "Impaled Human");
        addType(26,   Kind.OBSTACLE,   "POL1", "AB",    16,  false, "Twitching Impaled Human");
        addType(27,   Kind.OBSTACLE,   "POL1", "A",     16,  false, "Skull on a Pole");
        addType(28,   Kind.OBSTACLE,   "POL1", "A",     16,  false, "Skull Shish Kebob");
        addType(29,   Kind.OBSTACLE,   "POL1", "AB",    16,  false, "Pile of Skulls and Candles");

        addType(10,   Kind.DECORATION, "PLAY", "W",     16,  false, "Bloody Mess");
        addType(12,   Kind.DECORATION, "PLAY", "W",     16,  false, "Bloody Mess");
        addType(24,   Kind.DECORATION, "POL5", "A",     16,  false, "Pool of Blood and Flesh");
        addType(79,   Kind.DECORATION, "POB1", "A",     16,  false, "Pool of Blood");
        addType(80,   Kind.DECORATION, "POB2", "A",     16,  false, "Pool of Blood");
        addType(81,   Kind.DECORATION, "BRS1", "A",     16,  false, "Pool of Brains");
        addType(15,   Kind.DECORATION, "PLAY", "N",     16,  false, "Dead Marine");
        addType(18,   Kind.DECORATION, "POSS", "L",     20,  false, "Dead Former Human");
        addType(19,   Kind.DECORATION, "SPOS", "L",     20,  false, "Dead Former Sargeant");
        addType(20,   Kind.DECORATION, "TROO", "M",     20,  false, "Dead Imp");
        addType(21,   Kind.DECORATION, "SARG", "N",     30,  false, "Dead Demon");
        addType(22,   Kind.DECORATION, "HEAD", "L",     31,  false, "Dead Cacodemon");
        addType(23,   Kind.DECORATION, "SKUL", "K",     16,  true,  "Dead Lost Soul");

        addType(14,   Kind.SPECIAL,    "TFOG", "A-J",   20,  false, "Teleport Landing");
        addType(89,   Kind.SPECIAL,    "BOSF", "A",     20,  false, "Spawn Shooter");
        addType(87,   Kind.SPECIAL,    "FIRE", "A",     20,  false, "Spawn Spot");

        unknownDetails = new Details(Kind.UNKNOWN, null, null, (short) 20, false, "Unknown (???)");
    }

    private static void addType(int type, Kind kind, String spriteName, String frameSequence, int radius, boolean isHanging, String name) {
        typeDetails.put((short) type, new Details(kind, spriteName, frameSequence, (short) radius, isHanging, name));
    }


    private short    number;
    private Location location;
    private short    angle;
    private short    type;
    private short    flags;
    private Details  details;
    private Sprite   sprite;

    Thing(short number, Location location, short angle, short type, short flags) throws IOException {
        this.number   = number;
        this.location = location;
        this.angle    = angle;
        this.type     = type;
        this.flags    = flags;
        this.details  = typeDetails.get(type);

        if (details == null) {
            details = unknownDetails;
        }

        if (details.spriteName != null) {
            this.sprite = sprites.get(details.spriteName);

            if (sprite == null) {
                sprite = new Sprite(details.spriteName, details.isHanging);
                sprites.put(details.spriteName, sprite);
            }
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

    public String getFrameSequence() {
        return details.frameSequence;
    }

    public ImageProducer getImageProducer() throws IOException {
        return sprite.getImageProducer(details.frameSequence);
    }
}

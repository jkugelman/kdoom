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
        Kind   kind;
        String spriteName, frameSequence;
        short  radius;
        String name;
        
        public Details(Kind kind, String spriteName, String frameSequence, short radius, String name) {
            this.kind          = kind;
            this.spriteName    = spriteName;
            this.frameSequence = frameSequence;
            this.radius        = radius;
            this.name          = name;
        }
    }

    private static Map<Short, Details> typeDetails;
    private static Details             unknownDetails;

    static {
        typeDetails = new HashMap<Short, Details>(); 

        addType(1,    Kind.PLAYER,     "PLAY", "A1A2A3A4A3A2", 16, "Player 1 Start");
        addType(2,    Kind.PLAYER,     "PLAY", "A1",           16, "Player 2 Start");
        addType(3,    Kind.PLAYER,     "PLAY", "A1",           16, "Player 3 Start");
        addType(4,    Kind.PLAYER,     "PLAY", "A1",           16, "Player 4 Start");
        addType(11,   Kind.PLAYER,     "PLAY", "A1",           16, "Deathmatch Start");
                                                  
        addType(3004, Kind.MONSTER,    "POSS", "A1",           20, "Former Human");
        addType(9,    Kind.MONSTER,    "SPOS", "A1",           20, "Former Sargeant"); 
        addType(65,   Kind.MONSTER,    "CPOS", "A1",           20, "Heavy Weapon Dude"); 
        addType(3001, Kind.MONSTER,    "TROO", "A1",           20, "Imp"); 
        addType(3002, Kind.MONSTER,    "SARG", "A1",           30, "Demon"); 
        addType(58,   Kind.MONSTER,    "SARG", "A1",           30, "Spectre"); 
        addType(3006, Kind.MONSTER,    "SKUL", "A1",           16, "Lost Soul"); 
        addType(3005, Kind.MONSTER,    "HEAD", "A1",           31, "Cacodemon"); 
        addType(69,   Kind.MONSTER,    "BOS2", "A1",           24, "Hell Knight"); 
        addType(3003, Kind.MONSTER,    "BOSS", "A1",           24, "Baron of Hell"); 
        addType(68,   Kind.MONSTER,    "BSPI", "A1",           64, "Arachnotron"); 
        addType(71,   Kind.MONSTER,    "PAIN", "A1",           31, "Pain Elemental"); 
        addType(66,   Kind.MONSTER,    "SKEL", "A1",           20, "Revenant"); 
        addType(67,   Kind.MONSTER,    "FATT", "A1",           48, "Mancubus"); 
        addType(64,   Kind.MONSTER,    "VILE", "A1",           20, "Arch-Vile"); 
        addType(7,    Kind.MONSTER,    "SPID", "A1",           128,"Spider Mastermind"); 
        addType(16,   Kind.MONSTER,    "CYBR", "A1",           40, "Cyberdemon"); 
        addType(88,   Kind.MONSTER,    "BBRN", "A1",           16, "Boss Brain");
        addType(84,   Kind.MONSTER,    "SSWV", "A1",           20, "Wolfenstein SS"); 
                                                  
        addType(2005, Kind.WEAPON,     "CSAW", "A0",           20, "Chainsaw");
        addType(2001, Kind.WEAPON,     "SHOT", "A0",           20, "Shotgun");
        addType(82,   Kind.WEAPON,     "SGN2", "A0",           20, "Double-Barreled Shotgun");
        addType(2002, Kind.WEAPON,     "MGUN", "A0",           20, "Chaingun");
        addType(2003, Kind.WEAPON,     "LAUN", "A0",           20, "Rocket Launcher");
        addType(2004, Kind.WEAPON,     "PLAS", "A0",           20, "Plasma Gun");
        addType(2006, Kind.WEAPON,     "BFUG", "A0",           20, "BFG 9000");
                                                  
        addType(2007, Kind.AMMO,       "CLIP", "A0",           8,  "Ammo Clip");
        addType(2008, Kind.AMMO,       "SHEL", "A0",           8,  "Shotgun Shells");
        addType(2010, Kind.AMMO,       "ROCK", "A0",           8,  "Rocket");
        addType(2047, Kind.AMMO,       "CELL", "A0",           8,  "Cell Charge");
        addType(2048, Kind.AMMO,       "AMMO", "A0",           16, "Box of Ammo");
        addType(2049, Kind.AMMO,       "SBOX", "A0",           16, "Box of Shells");
        addType(2046, Kind.AMMO,       "BROK", "A0",           16, "Box of Rockets");
        addType(17,   Kind.AMMO,       "CELP", "A0",           16, "Cell Charge Pack");
        addType(8,    Kind.AMMO,       "BPAK", "A0",           16, "Backpack");
                                                  
        addType(2011, Kind.HEALTH,     "STIM", "A0",           12, "Stimpak +10%");
        addType(2012, Kind.HEALTH,     "MEDI", "A0",           16, "Medikit +25%");
        addType(2014, Kind.HEALTH,     "BON1", "A0B0C0D0C0B0", 8,  "Health Potion +1%");
        addType(2015, Kind.ARMOR,      "BON2", "A0B0C0D0C0B0", 8,  "Spirit Armor +1%");
        addType(2018, Kind.ARMOR,      "ARM1", "A0B0",         16, "Green Armor +100%");
        addType(2019, Kind.ARMOR,      "ARM2", "A0B0",         16, "Blue Armor +200%");
        addType(83,   Kind.POWER_UP,   "MEGA", "A0B0C0D0",     16, "Megasphere");
        addType(2013, Kind.POWER_UP,   "SOUL", "ABCDCB",       16, "Soulsphere");
        addType(2022, Kind.POWER_UP,   "PINV", "A0B0C0D0",     16, "Invulnerability Sphere");
        addType(2023, Kind.POWER_UP,   "PSTR", "A0",           16, "Berkserker Pack");
        addType(2024, Kind.POWER_UP,   "PINS", "A0B0C0D0",     16, "Invisibility Sphere");
        addType(2025, Kind.POWER_UP,   "SUIT", "A0",           16, "Radiation Suit");
        addType(2026, Kind.POWER_UP,   "PMAP", "A0B0C0D0C0B0", 16, "Computer Map");
        addType(2045, Kind.POWER_UP,   "PVIS", "A0B0",         16, "Light Amplification Goggles");
                                                  
        addType(5,    Kind.KEY,        "BKEY", "A0B0",         16, "Blue Key");
        addType(40,   Kind.KEY,        "BSKU", "A0B0",         16, "Blue Skullkey");
        addType(13,   Kind.KEY,        "RKEY", "A0B0",         16, "Red Key");
        addType(38,   Kind.KEY,        "RSKU", "A0B0",         16, "Red Skullkey");
        addType(6,    Kind.KEY,        "YKEY", "A0B0",         16, "Yellow Key");
        addType(39,   Kind.KEY,        "YSKU", "A0B0",         16, "Yellow Skullkey");
                                                  
        addType(2035, Kind.OBSTACLE,   "BAR1", "A0B0",         10, "Barrel");
        addType(72,   Kind.OBSTACLE,   "KEEN", "A0",           16, "Commander Keen"); 
                                                  
        addType(48,   Kind.OBSTACLE,   "ELEC", "A0",           16, "Tall Techno Pillar");
        addType(30,   Kind.OBSTACLE,   "COL1", "A0",           16, "Tall Green Pillar");
        addType(32,   Kind.OBSTACLE,   "COL3", "A0",           16, "Tall Red Pillar");
        addType(31,   Kind.OBSTACLE,   "COL2", "A0",           16, "Short Green Pillar");
        addType(36,   Kind.OBSTACLE,   "COL5", "A0B0",         16, "Short Green Pillar with Beating Heart");
        addType(33,   Kind.OBSTACLE,   "COL4", "A0",           16, "Short Red Pillar");
        addType(37,   Kind.OBSTACLE,   "COL6", "A0",           16, "Short Red Pillar with Skull");
        addType(47,   Kind.OBSTACLE,   "SMIT", "A0",           16, "Stalagmite");
        addType(43,   Kind.OBSTACLE,   "TRE1", "A0",           16, "Burnt Tree");
        addType(54,   Kind.OBSTACLE,   "TRE2", "A0",           32, "Large Brown Tree");
                                                  
        addType(2028, Kind.OBSTACLE,   "COLU", "A0",           16, "Floor Lamp");
        addType(85,   Kind.OBSTACLE,   "TLMP", "A0B0C0D0",     16, "Tall Techno Floor Lamp");
        addType(86,   Kind.OBSTACLE,   "TLP2", "A0B0C0D0",     16, "Short Techno Floor Lamp");
        addType(34,   Kind.OBSTACLE,   "CAND", "A0",           16, "Candle");
        addType(35,   Kind.OBSTACLE,   "CBRA", "A0",           16, "Candelabra");
        addType(44,   Kind.OBSTACLE,   "TBLU", "A0B0C0D0",     16, "Tall Blue Firestick");
        addType(45,   Kind.OBSTACLE,   "TGRE", "A0B0C0D0",     16, "Tall Green Firestick");
        addType(46,   Kind.OBSTACLE,   "TRED", "A0B0C0D0",     16, "Tall Red Firestick");
        addType(55,   Kind.OBSTACLE,   "SMBT", "A0B0C0D0",     16, "Short Blue Firestick");
        addType(56,   Kind.OBSTACLE,   "SMGT", "A0B0C0D0",     16, "Short Green Firestick");
        addType(57,   Kind.OBSTACLE,   "SMRT", "A0B0C0D0",     16, "Short Red Firestick");
        addType(70,   Kind.OBSTACLE,   "FCAN", "A0B0C0",       10, "Burning Barrel");
                                                  
        addType(41,   Kind.OBSTACLE,   "CEYE", "A0B0C0B0",     16, "Evil Eye");
        addType(42,   Kind.OBSTACLE,   "FSKU", "A0B0C0",       16, "Floating Skull");
                                                  
        addType(49,   Kind.OBSTACLE,   "GOR1", "A0B0C0B0",     16, "Hanging Body, Twitching");
        addType(63,   Kind.DECORATION, "GOR1", "A0B0C0B0",     16, "Hanging Body, Twitching");
        addType(50,   Kind.OBSTACLE,   "GOR2", "A0",           16, "Hanging Body, Arms Out");
        addType(59,   Kind.DECORATION, "GOR2", "A0",           16, "Hanging Body, Arms Out");
        addType(51,   Kind.OBSTACLE,   "GOR3", "A0",           16, "Hanging Victim, One-Legged");
        addType(61,   Kind.DECORATION, "GOR3", "A0",           16, "Hanging Victim, One-Legged");
        addType(52,   Kind.OBSTACLE,   "GOR4", "A0",           16, "Hanging Pair of Legs");
        addType(60,   Kind.DECORATION, "GOR4", "A0",           16, "Hanging Pair of Legs");
        addType(53,   Kind.OBSTACLE,   "GOR5", "A0",           16, "Hanging Leg");
        addType(62,   Kind.DECORATION, "GOR5", "A0",           16, "Hanging Leg");
        addType(73,   Kind.OBSTACLE,   "HDB1", "A0",           16, "Hanging Body, Guts Removed");
        addType(74,   Kind.OBSTACLE,   "HDB2", "A0",           16, "Hanging Body, Guts and Brain Removed");
        addType(76,   Kind.OBSTACLE,   "HDB3", "A0",           16, "Hanging Torso, Looking Down");
        addType(76,   Kind.OBSTACLE,   "HDB4", "A0",           16, "Hanging Torso, Open Skull");
        addType(77,   Kind.OBSTACLE,   "HDB5", "A0",           16, "Hanging Torso, Looking Up");
        addType(78,   Kind.OBSTACLE,   "HDB6", "A0",           16, "Hanging Torso, Brain Removed");
                                                  
        addType(25,   Kind.OBSTACLE,   "POL1", "A0",           16, "Impaled Human");
        addType(26,   Kind.OBSTACLE,   "POL1", "A0B0",         16, "Twitching Impaled Human");
        addType(27,   Kind.OBSTACLE,   "POL1", "A0",           16, "Skull on a Pole");
        addType(28,   Kind.OBSTACLE,   "POL1", "A0",           16, "Skull Shish Kebob");
        addType(29,   Kind.OBSTACLE,   "POL1", "A0B0",         16, "Pile of Skulls and Candles");
                                                  
        addType(10,   Kind.DECORATION, "PLAY", "W0",           16, "Bloody Mess");
        addType(12,   Kind.DECORATION, "PLAY", "W0",           16, "Bloody Mess");
        addType(24,   Kind.DECORATION, "POL5", "A0",           16, "Pool of Blood and Flesh");
        addType(79,   Kind.DECORATION, "POB1", "A0",           16, "Pool of Blood");
        addType(80,   Kind.DECORATION, "POB2", "A0",           16, "Pool of Blood");
        addType(81,   Kind.DECORATION, "BRS1", "A0",           16, "Pool of Brains");
        addType(15,   Kind.DECORATION, "PLAY", "N1",           16, "Dead Marine");
        addType(18,   Kind.DECORATION, "POSS", "L1",           20, "Dead Former Human");
        addType(19,   Kind.DECORATION, "SPOS", "L1",           20, "Dead Former Sargeant");
        addType(20,   Kind.DECORATION, "TROO", "M1",           20, "Dead Imp");
        addType(21,   Kind.DECORATION, "SARG", "N1",           30, "Dead Demon");
        addType(22,   Kind.DECORATION, "HEAD", "L1",           31, "Dead Cacodemon");
        addType(23,   Kind.DECORATION, "SKUL", "K1",           16, "Dead Lost Soul");
                                                   
        addType(14,   Kind.SPECIAL,    "TFOG", null,           20, "Teleport Landing");
        addType(89,   Kind.SPECIAL,    "BOSF", null,           20, "Spawn Shooter");
        addType(87,   Kind.SPECIAL,    "FIRE", null,           20, "Spawn Spot");

        unknownDetails = new Details(Kind.UNKNOWN, null, null, (short) 20, "Unknown (???)");
    }

    private static void addType(int type, Kind kind, String spriteName, String frameSequence, int radius, String name) {
        typeDetails.put((short) type, new Details(kind, spriteName, frameSequence, (short) radius, name));
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
        
        if (details.spriteName != null) {
            this.sprite = new Sprite(wad, details.spriteName);
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

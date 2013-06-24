package me.marwzoor.skillfletchbow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SkillFletchBow extends ActiveSkill
{
  public static Heroes plugin;
  public static SkillFletchBow skill;

  public SkillFletchBow(Heroes instance)
  {
    super(plugin, "FletchBow");
    plugin=instance;
    skill=this;
    setDescription("You fletch yourself a bow$enchant");
    setUsage("/skill fletchbow");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill fletchbow", "skill fbow" });
    setTypes(new SkillType[] { SkillType.ITEM, SkillType.SUMMON, SkillType.SILENCABLE });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(SkillSetting.AMOUNT.node(), Integer.valueOf(1));
    node.set("enchantment", "noenchant");
    node.set("enchantment-level", -1);
    node.set("namelist", "bows1");
    
	File file = new File(plugin.getDataFolder() + "/bows.yml");
	
	if(!file.exists())
	{
		try
		{
			file.createNewFile();
		}
		catch(Exception e)
		{

		}
	}
	
	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	ConfigurationSection section = config.createSection("bows1");
	
	section.set("crude-oak-shortbow.name", "Crude Oak Shortbow");
	List<String> desc = new ArrayList<String>();
	desc.add("A poorly fletched bow, but by hands eager to use it against enemies.");
	section.set("crude-oak-shortbow.desc", desc);
	
	try
	{
		config.save(file);
	}
	catch(Exception e)
	{
		
	}
    
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
	File file = new File(plugin.getDataFolder() + "/bows.yml");
	
	if(!file.exists())
	{
		try
		{
			file.createNewFile();
		}
		catch(Exception e)
		{
			return SkillResult.FAIL;
		}
	}
	
	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	String lname = SkillConfigManager.getUseSetting(hero, skill, "namelist", "bows1");
	
	ConfigurationSection list = config.getConfigurationSection(lname);
	
	if(list==null)
	{
		return SkillResult.FAIL;
	}
	
	List<String> names = new ArrayList<String>();
	List<List<String>> lores = new ArrayList<List<String>>();
	
	for(String k : list.getKeys(true))
	{
		String key = k.split(".")[0];
		
		names.add(list.getString(key + ".name"));
		List<String> lore = list.getStringList(key + ".desc");
		lores.add(lore);
	}
	
	int amount = SkillConfigManager.getUseSetting(hero, this, "amount", 1, false);
	String enchname = SkillConfigManager.getUseSetting(hero, this, "enchantment", "noenchant");
	int enchlevel = SkillConfigManager.getUseSetting(hero, this, "enchantment-level", -1, false);
    Player player = hero.getPlayer();
    World world = player.getWorld();
    ItemStack dropItem = new ItemStack(Material.BOW, amount);
    if(enchname!="noenchant" && enchlevel!=-1)
    {
    Enchantment ench = Enchantment.getByName(enchname);
    
    	if(ench!=null)
    	{
    		ItemMeta im = dropItem.getItemMeta();
    		im.addEnchant(ench, enchlevel, true);
    		dropItem.setItemMeta(im);
    	}
    }
    ItemMeta im = dropItem.getItemMeta();
    
    int random = new Random().nextInt(names.size());
   	im.setDisplayName(getBowName(names.get(random)));
   	im.setLore(getBowLore(lores.get(random)));
   	dropItem.setItemMeta(im);
   	world.dropItem(player.getLocation(), dropItem);
   	Messaging.send(player, "You fletch yourself a" + ChatColor.WHITE + " Bow " + ChatColor.GRAY + "by the name " + getBowName(names.get(random)) + ChatColor.GRAY + "!");
    
   	return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
	  	String desc = getDescription();
	  	String enchname = SkillConfigManager.getUseSetting(hero, this, "enchantment", "noenchant");
	  	int enchlevel = SkillConfigManager.getUseSetting(hero, this, "enchantment-level", -1, false);
	  	if(enchname!="noenchant" && enchlevel!=-1)
	  	{
	  		desc = desc.replace("$enchant", " with the " + enchname + " enchant at level " + enchlevel + ".");
	  	}
	  	else
	  	{
	  		desc = desc.replace("$enchant", ".");
	  	}
   		return desc;
  }
  
  public String getBowName(String name)
  {
	  Random rand = new Random();
	  String[] cs = new String[]{"�a", "�b", "�c", "�d", "�e"};
	  
	  return "�r" + cs[rand.nextInt(cs.length)] + name;
  }
  
  public String getBowName(String name, String format)
  {
	  return format + name;
  }
  
  public List<String> getBowLore(String lore)
  {
	  return Arrays.asList("�r�8�o" + lore);
  }
  
  public List<String> getBowLore(List<String> lore)
  {
	  List<String> l = new ArrayList<String>();
	  
	  for(String s : lore)
	  {
		  l.add("�r�8�o" + s);
	  }
	  
	  return l;
  }
  
  public List<String> getBowLore(String lore, String format)
  {
	  return Arrays.asList(format + lore);
  }
}
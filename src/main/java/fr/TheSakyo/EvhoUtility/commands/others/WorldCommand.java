package fr.TheSakyo.EvhoUtility.commands.others;

import java.io.File;
import java.io.IOException;

import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.config.ConfigFileManager;
import fr.TheSakyo.EvhoUtility.config.WorldHandler;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.minecraft.ChatFormatting;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.worldgenerator.FlatGenerator;
import fr.TheSakyo.EvhoUtility.utils.worldgenerator.VoidGenerator;
import org.bukkit.generator.ChunkGenerator;

public class WorldCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public WorldCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	String GI = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString(); // Code couleur utile pour les messages au tchat //

	// Variables ChatFormatting //
	String W = ChatFormatting.WHITE.toString();
	String GB = ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString();
	String Y = ChatFormatting.YELLOW.toString();
	// Variables ChatFormatting //


	/***********************************************/
	/* PARTIE COMMANDE POUR LA GESTION DE MONDE */
	/***********************************************/
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

		if(sender instanceof Player p) {

			if(!p.hasPermission("evhoutility.world")) {

				p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
				return true;
			}
		}

		if(args.length == 0) {

			if(sender instanceof Player p) {

				p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez entrer des arguments ! <create, remove, load, tp, list ou info> [<worldname>] [<worldtype>]");

            } else {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez entrer des arguments ! <create, remove, load, list ou info> [<worldname>] [<worldtype>]");
				sender.sendMessage(" ");
				sender.sendMessage(main.prefix + ChatFormatting.RED + "L'Argument <tp> fonctionne qu'en jeux !");

            }

            return true;

        } else if(args.length == 1) {

			if(sender instanceof Player p) {

				if(args[0].equalsIgnoreCase("list")) listOfExistingWorlds(p);
				else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("load")) {

					p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world " + args[0].toLowerCase() + " <worldname>");
					return true;

				} else if(args[0].equalsIgnoreCase("create")) {

					p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> <worldtype> ['structure:'<true|false>]");
					return true;

				} else if(args[0].equalsIgnoreCase("info")) {

					p.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
					p.sendMessage(" ");
					p.sendMessage(" ");

					p.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Information du monde :");

					p.sendMessage(" ");
					p.sendMessage(ChatFormatting.WHITE + "- " + GB + "Nom du monde" + W + " (" + Y + p.getWorld().getName() + W + ")");
					p.sendMessage(" ");
					p.sendMessage(ChatFormatting.WHITE + "- " + GB + "Nombre de joueur(s) dans le monde" + W + " (" + Y + p.getWorld().getPlayers().size() + W + ")");
					p.sendMessage(" ");

					/******************************************/

					CraftWorld cw = (CraftWorld)p.getWorld();

					/******************************************/

					if(p.getWorld().getGenerator() != null && p.getWorld().getPopulators().isEmpty()) p.sendMessage(ChatFormatting.WHITE + "- " + GB + "Type du monde" + W + " (" + Y + cw.getEnvironment().name() + ":VOID" + W + ")");
					else p.sendMessage(ChatFormatting.WHITE + "- " + GB + "Type du monde" + W + " (" + Y + p.getWorld().getEnvironment().name() + ":" + cw.getWorldType().getName() + W + ")");

					/******************************************/

					p.sendMessage(" ");
					p.sendMessage(" ");
					p.sendMessage(ChatFormatting.GRAY + "===========================");


				} else { p.performCommand("world"); }

			} else {

				if(args[0].equalsIgnoreCase("list")) listOfExistingWorlds(sender);
				else if(args[0].equalsIgnoreCase("create")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> <worldtype> ['structure:'<true|false>]");
				else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("info")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world " + args[0].toLowerCase() + " <worldname>");
				else if(args[0].equalsIgnoreCase("tp")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour effectuer cette commande !");
				else Bukkit.getServer().dispatchCommand(sender, "world");
			}

		} else if(args.length == 2) {

			String worldName = args[1];

			if(args[0].equalsIgnoreCase("create")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> <worldtype> ['structure:'<true|false>]");

			else if(args[0].equalsIgnoreCase("remove")) {

				ConfigurationSection WorldSection = ConfigFile.getConfigurationSection(main.worldConfig, "serverworlds");

							/* ------------------------------------------ */

				if(WorldSection.contains(worldName)) {

						ConfigFile.removeKey(main.worldConfig, "serverworlds." + worldName);
						ConfigFile.saveConfig(main.worldConfig);
						sender.sendMessage(main.prefix + GI + "Le monde " + ChatFormatting.GOLD + worldName + GI + " a été supprimer du fichier de configuration !");

				} else sender.sendMessage(main.prefix + GI + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " est introuvable dans le fichier de configuation !");

							/* ------------------------------------------ */

				if(Bukkit.getServer().getWorld(worldName) != null) {

					if(Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_nether") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_the_end")) {

					  sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas supprimer un monde par défaut ! Vous pouvez supprimer ce genre de monde manuellement !");
					  return true;
					}

					/******************************************************************************/

					for(Player player : Bukkit.getServer().getOnlinePlayers()) {

						if(player.getWorld() == Bukkit.getServer().getWorld(worldName)) {

						  sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas supprimer ce monde si des joueurs se trouve à l'intérieur !");
						  return true;
						}
					}

					/******************************************************/

					Bukkit.getServer().unloadWorld(worldName, false);

					/******************************************************/

					// Supprime le dossier du Monde, s'il existe //
					File file = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/" + worldName + "/");
					if(file.exists()) {

						try { FileUtils.deleteDirectory(file); }
						catch(IOException e) { file.delete(); }
					}
					// Supprime le dossier du Monde, s'il existe //

					/******************************************************************************/

					sender.sendMessage(main.prefix + GI + "Le monde " + ChatFormatting.GOLD + worldName + GI + " a été supprimer du serveur !");
					UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin

				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " n'éxiste pas !");

			} else if(args[0].equalsIgnoreCase("load")) {

				File file = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/" + worldName + "/");

				/**************************************************/

				if(file.exists()) {

				   for(Player player : Bukkit.getServer().getOnlinePlayers()) {

					  if(player.getWorld() == Bukkit.getServer().getWorld(worldName)) {

						 sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas recharger ce monde si des joueurs se trouve à l'intérieur !");
						 return true;
					  }
				   }

				  /******************************************************/

				   sender.sendMessage(main.prefix + GI + "Rechargement du monde " + ChatFormatting.GOLD.toString() + ChatFormatting.ITALIC.toString() + worldName + GI + "......");

				   // Recharge le monde souhait
				   new WorldHandler(main, worldName);

				  /******************************************************/

				  UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin
				  sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été recharger !");

				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " est introuvable !");

			} else if(args[0].equalsIgnoreCase("tp")) {

				if(sender instanceof Player p) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

						p.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " n'éxiste pas !");

					} else {

						if(Bukkit.getServer().getWorld(worldName) == p.getWorld()) {

							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous êtes déja dans le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " !");
							return true;
						}

						/***********************************************/

						if(Bukkit.getServer().getWorld(worldName).getEnvironment().equals(Environment.NETHER)) p.teleport(Bukkit.getServer().getWorld(worldName).getSpawnLocation().zero());
						else p.teleport(Bukkit.getServer().getWorld(worldName).getSpawnLocation());

						/***********************************************/

						p.sendMessage(main.prefix + GI + "Vous avez été téléporter dans le monde " + ChatFormatting.GOLD + worldName);
					}

				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous téléportez dans un monde !");

			} else if(args[0].equalsIgnoreCase("list")) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world list");

			else if(args[0].equalsIgnoreCase("info")) {

				World world = Bukkit.getServer().getWorld(worldName);
				CraftWorld cw = (CraftWorld)world;

				if(world == null) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " n'éxiste pas !");

				} else {

					sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
					sender.sendMessage(" ");
					sender.sendMessage(" ");

					sender.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Information du monde :");

					sender.sendMessage(" ");
					sender.sendMessage(ChatFormatting.WHITE + "- " + GB + "Nom du monde" + W + " (" + Y + world.getName() + W + ")");
					sender.sendMessage(" ");
					sender.sendMessage(ChatFormatting.WHITE + "- " + GB + "Nombre de joueur(s) dans le monde" + W + " (" + Y + world.getPlayers().size() + W + ")");
					sender.sendMessage(" ");

					if(world.getGenerator() != null && world.getPopulators().isEmpty()) sender.sendMessage(ChatFormatting.WHITE + "- " + GB + "Type du monde" + W + " (" + Y + world.getEnvironment().name() + ":VOID" + W + ")");
					else sender.sendMessage(ChatFormatting.WHITE + "- " + GB + "Type du monde" + W + " (" + Y + world.getEnvironment().name() + ":" + cw.getWorldType().getName() + W + ")");

					sender.sendMessage(" ");
					sender.sendMessage(" ");
					sender.sendMessage(ChatFormatting.GRAY + "===========================");
				}

			} else Bukkit.getServer().dispatchCommand(sender, "world");

		} else if(args.length == 3) {

			String worldName = args[1];
			String worldType = args[2];

			String GDI = ChatFormatting.GOLD.toString() + ChatFormatting.ITALIC.toString();

			/******************************************************************/

			if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("load")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world " + args[0].toLowerCase() + " <worldname>");
				return true;

			} else if(args[0].equalsIgnoreCase("list")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world list");
				return true;

			} else if(args[0].equalsIgnoreCase("create")) {

				if(Bukkit.getWorld(worldName) != null) {

					if(Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_nether") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_the_end")) {

					  sender.sendMessage(main.prefix + ChatFormatting.RED + "Le nom que vous avez définit est un nom utilisé comme monde par défaut, essayez un autre nom !");
					  return true;
					}
				}

				if(worldType.equalsIgnoreCase("VOID")) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

					   sender.sendMessage(main.prefix + GI + "Création du monde " + GDI + worldName + GI + "......");
					   getVOIDWorldGenerator(worldName, WorldType.FLAT, Environment.NORMAL, new VoidGenerator());
					   sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été créer !");

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " éxiste déjà !");

					return true;

				} else if(worldType.equalsIgnoreCase("NORMAL") || worldType.equalsIgnoreCase("NETHER")
						|| worldType.equalsIgnoreCase("END") || worldType.equalsIgnoreCase("FLAT")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> " + worldType + " <'structure:' true|false>");
					return true;

				} else {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Le type de monde est pas valide ! Essayez <VOID, NORMAL, FLAT, NETHER ou END>");
					return true;
				}

			} else if(args[0].equalsIgnoreCase("info")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world info [<worldname>]");
				return true;
			}

			/**************************************/

			Bukkit.getServer().dispatchCommand(sender, "world");

		} else if(args.length == 4) {

			String worldName = args[1];
			String worldType = args[2];
			Boolean structure = Boolean.parseBoolean(args[3]);
			String GDI = ChatFormatting.GOLD.toString() + ChatFormatting.ITALIC.toString();

			/***************************************/

			/* Vérifie si l'argument "structure" est bien égal à 'true' ou 'false' */
			if(args[3].equalsIgnoreCase("true")) structure.equals(true);
			else if(args[3].equalsIgnoreCase("false")) structure.equals(false);
			else {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> <worldtype> ['structure:'<true|false>]");
				return true;
			}
			/* Vérifie si l'argument "structure" est bien égal à 'true' ou 'false' */

			/***************************************/

			if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("load")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world " + args[0].toLowerCase() + " <worldname>");
				return true;

			} else if(args[0].equalsIgnoreCase("list")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world list");
				return true;
			}

			else if(args[0].equalsIgnoreCase("create")) {

				if(Bukkit.getWorld(worldName) != null) {

					if(Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_nether") || Bukkit.getServer().getWorld(worldName).getName().equalsIgnoreCase("world_the_end")) {

						sender.sendMessage(main.prefix + ChatFormatting.RED + "Le nom que vous avez définit est un nom utilisé comme monde par défaut, essayez un autre nom !");
						return true;
					}
				}

				if(worldType.equalsIgnoreCase("VOID")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> VOID");
					return true;

				} else if(worldType.equalsIgnoreCase("NORMAL")) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

						sender.sendMessage(main.prefix + GI + "Création du monde " + GDI + worldName + GI + "......");
						getDefaultWorldGenerator(worldName, WorldType.LARGE_BIOMES, Environment.NORMAL, null, structure);
						sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été créer !");

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " éxiste déjà !");

					return true;

				} else if(worldType.equalsIgnoreCase("FLAT")) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

						sender.sendMessage(main.prefix + GI + "Création du monde " + GDI + worldName + GI + "......");
						getSuperFlatWorldGenerator(worldName, WorldType.FLAT, Environment.NORMAL, new FlatGenerator(), structure);
						sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été créer !");

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " éxiste déjà !");

					return true;

				} else if(worldType.equalsIgnoreCase("NETHER")) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

						sender.sendMessage(main.prefix + GI + "Création du monde " + GDI + worldName + GI + "......");
						getDefaultWorldGenerator(worldName, WorldType.NORMAL, Environment.NETHER, null, structure);
						sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été créer !");

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " éxiste déjà !");

					return true;

				} else if(worldType.equalsIgnoreCase("END")) {

					if(Bukkit.getServer().getWorld(worldName) == null) {

						sender.sendMessage(main.prefix + GI + "Création du monde " + GDI + worldName + GI + "......");
						getDefaultWorldGenerator(worldName, WorldType.NORMAL, Environment.THE_END, null, structure);
						sender.sendMessage(main.prefix + ChatFormatting.GREEN + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.GREEN + " a été créer !");

					} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le monde " + ChatFormatting.GOLD + worldName + ChatFormatting.RED + " éxiste déjà !");

					return true;

				} else {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Le type de monde est pas valide ! Essayez <VOID, NORMAL, FLAT, NETHER ou END>");
					return true;
				}

			} else if(args[0].equalsIgnoreCase("info")) {

				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world info [<worldname>]");
				return true;
			}

			/**************************************/

			Bukkit.getServer().dispatchCommand(sender, "world");

		} else {

				if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("load")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world " + args[0].toLowerCase() + " <worldname>");
					return true;

				} else if(args[0].equalsIgnoreCase("list")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world list");
					return true;
				}

				else if(args[0].equalsIgnoreCase("info")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world info [<worldname>]");
					return true;
				}

				else if(args[0].equalsIgnoreCase("create")) {

					sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /world create <worldname> <worldtype> ['structure:'<true|false>]");
					return true;
				}

				/**************************************/

			Bukkit.getServer().dispatchCommand(sender, "world");
		}

		return false;
	}
	/***********************************************/
	/* PARTIE COMMANDE POUR LA GESTION DE MONDE */
	/***********************************************/




	/**********************************************/
	/* PARTIE MÉTHODES POUR LA CRÉATION DE MONDES */
	/**********************************************/

	// Créer un monde (option monde vide) //
	public void getVOIDWorldGenerator(String worldName, WorldType worldType, Environment environment, VoidGenerator generator) {

		WorldCreator wc = new WorldCreator(worldName);

		wc.type(worldType);
	    wc.environment(environment);
	    wc.generator(generator);
		wc.generateStructures(false);
	    wc.createWorld();

	    World world = Bukkit.getServer().getWorld(worldName); // Récupère le monde créé

		// Ajoute au point de spawn du monde des blocs en bedrock dans un rayon de 2.
	    CustomMethod.setTypeBlocks(world.getSpawnLocation().getBlock(), 2, Material.BEDROCK);

		UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin

	}
	// Créer un monde (option monde vide) //


	// Créer un monde (option monde SuperPlat) //
	public void getSuperFlatWorldGenerator(String worldName, WorldType worldType, Environment environment, FlatGenerator generator, boolean structure) {

		WorldCreator wc = new WorldCreator(worldName);

		wc.type(worldType);
	    wc.environment(environment);
	    wc.generator(generator);
	    wc.generateStructures(structure);
	    wc.createWorld();

		UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin
	}
	// Créer un monde (option monde SuperPlat) //


	// Créer un monde (option basique) //
	public void getDefaultWorldGenerator(String worldName, WorldType worldType, Environment environment, ChunkGenerator generator, boolean structure) {

		WorldCreator wc = new WorldCreator(worldName);

		wc.type(worldType);
	    wc.environment(environment);
	    if(generator != null) wc.generator(generator);
		wc.generateStructures(structure);
	    wc.createWorld();

		UtilityConfigWorld(); //Recharge le fichier de configuration des mondes du Plugin

	}
	// Créer un monde (option basique) //

	/**********************************************/
	/* PARTIE MÉTHODES POUR LA CRÉATION DE MONDES */
	/**********************************************/


	/********************************************************************************************/
	/* PARTIE ENREGISTREMENT DES MONDES DU SERVEUR VERS UN FICHIER DE CONFIGURATION "world.yml" */
	/*******************************************************************************************/
	public static void UtilityConfigWorld() {

		UtilityMain mainInstance = UtilityMain.getInstance();
		ConfigFileManager.loadUtilityWorldConfig(); //Recharge le fichier de configuration 'world.yml' du Plugin
		ConfigFileManager.clearKeyUtilityWorldConfig(); //Supprime les éléments du fichier de configuration 'world.yml' du Plugin

		/************************************************/
		/************************************************/

		// Recréer proprement les sauvegarde de chaque monde enregistré le fichier de configuration 'world.yml' //
        for(World w : Bukkit.getServer().getWorlds()) {

            CraftWorld cw = (CraftWorld) w;

            ChunkGenerator generator = w.getGenerator();

            ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".UID", w.getUID().toString());
            ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".Type", cw.getWorldType().getName());
            ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".Environment", w.getEnvironment().name());

			/************************************************/

            if(generator == null) ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".Generator", "none");
            else ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".Generator", generator.getClass().getName());

			/************************************************/

			ConfigFile.set(mainInstance.worldConfig, "serverworlds." + w.getName() + ".Structure", Boolean.toString(w.canGenerateStructures()));
        }
		// Recréer proprement les sauvegarde de chaque monde enregistré le fichier de configuration 'world.yml' //

		/************************************************/
		/************************************************/

		ConfigFile.saveConfig(mainInstance.worldConfig); // Sauvegarde le fichier de configuration 'world.yml'
	}
	/********************************************************************************************/
	/* PARTIE ENREGISTREMENT DES MONDES DU SERVEUR VERS UN FICHIER DE CONFIGURATION "world.yml" */
	/*******************************************************************************************/


	/**************************************************************************************/
	/**************************************************************************************/

	private void listOfExistingWorlds(CommandSender sender) {

		if(Bukkit.getServer().getWorlds().isEmpty()) {

			sender.sendMessage(main.prefix + ChatFormatting.RED + "Aucun monde(s) dans le serveur !");

		} else if(Bukkit.getServer().getWorlds().size() == 1) {

			sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Il y'a seulement le monde " + ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + Bukkit.getServer().getWorlds().get(0) + ChatFormatting.GRAY + " dans le serveur !");

		} else {

			sender.sendMessage(ChatFormatting.GRAY + "========= " + main.prefix + ChatFormatting.GRAY + "=========");
			sender.sendMessage(" ");
			sender.sendMessage(" ");

			sender.sendMessage(ChatFormatting.AQUA.toString() + ChatFormatting.UNDERLINE.toString() + "Liste des mondes dans le serveur :");

			/*************************************************/
			/*************************************************/

			for(World world : Bukkit.getServer().getWorlds()) {

				CraftWorld cw = (CraftWorld)world;
				sender.sendMessage(" ");

				/******************************************/

				if(world.getGenerator() != null && world.getPopulators().isEmpty()) sender.sendMessage(ChatFormatting.WHITE + "- " + GB + world.getName() + W + " (" + Y + world.getEnvironment().name() + ":VOID" + W + ")");
				else sender.sendMessage(ChatFormatting.WHITE + "- " + GB + world.getName() + W + " (" + Y + world.getEnvironment().name() + ":" + cw.getWorldType().getName() + W + ")");
			}

			/*************************************************/
			/*************************************************/

			sender.sendMessage(" ");
			sender.sendMessage(" ");
			sender.sendMessage(ChatFormatting.GRAY + "===========================");
		}
	}
}

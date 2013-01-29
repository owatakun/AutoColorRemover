package com.github.owatakun.AutoColorRemover;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

public class AutoColorRemover extends JavaPlugin implements Listener{

	static FileConfiguration config;

	// 起動
	public void onEnable() {
		// コマンドをサーバーに登録
		getCommand("autocolorremover").setExecutor(this);
		// イベント購読をサーバーに登録
		getServer().getPluginManager().registerEvents(this, this);
		//起動メッセージ
		getLogger().info("AutoColorRemover v" + getDescription().getVersion() + " has been enabled!");
		// ColorMe起動チェック
		Plugin cm = getServer().getPluginManager().getPlugin("ColorMe");
		if ( cm != null && cm instanceof ColorMe ) {
		} else {
			getLogger().severe("This plugin requires ColorMe!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// Configチェック
		try {
			config = getConfig();
			getDataFolder().mkdir();
			File Config = new File(getDataFolder() + File.separator + "config.yml");
			Config.createNewFile();
			if(!config.contains("Enable")) { config.set("Enable", true); }
			saveConfig();
		} catch(IOException e1) {
			e1.printStackTrace();
		}
	}

	// 終了
	public void onDisable() {
		getLogger().info("AutoColorRemover v" + getDescription().getVersion() + " has been disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		// command : autocolorremover (acr)
		if (cmd.getName().equalsIgnoreCase("autocolorremover")){
			if (args.length != 0) {
				// command : autocolorremover <on|off>
				if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
					// on|offをboolean型に変換
					Boolean check;
					if (args[0].equalsIgnoreCase("on")) {
						check = true;
					} else {
						check = false;
					}
					// メモリ上に保存
					this.getConfig().set("Enable", check);
					// ディスクに保存
					this.saveConfig();
					// 処理完了メッセージ
					String Enable = config.getString("Enable");
					sender.sendMessage("§2[§9AutoColorRemover§2] Enable : §r" + Enable);
					return true;
				} else {
					// コマンドの形式が違ったらそれを知らせる
					sender.sendMessage("/acr <on|off> - AutoColorRemover Enable/Disable");
					return true;
				}

			}
			// ConfigからEnableの状態を見る
			String Enable = config.getString("Enable");
			// 現在状態を知らせる
			sender.sendMessage("§2[§9AutoColorRemover§2] Enable : §r" + Enable);
			return true;
		} else {
			// コマンドの形式が違ったらそれを知らせる
			sender.sendMessage("/acr <on|off> - AutoColorRemover Enable/Disable");
			return true;
		}
	}

	@EventHandler
	// プレイヤーが死んだ時に実行
	public void onPlayerDeathEvent(PlayerDeathEvent event){
		// 有効状態を読み込み
		Boolean Enable = config.getBoolean("Enable");
		// 有効ならば
		if (Enable){
			// 死亡プレイヤーのデータ取得
			Player player = event.getEntity();
			// ColorMeAPI準備
			Actions actions = new Actions((ColorMe) getServer().getPluginManager().getPlugin("ColorMe"));
			// 死亡プレイヤーの色を消す
			actions.remove(player.getName(), "default", "colors");
			// 死亡プレイヤーの表示を更新する
			actions.checkNames(player.getName(), "default");
		}
	}
}
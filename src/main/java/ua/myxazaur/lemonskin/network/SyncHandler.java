package ua.myxazaur.lemonskin.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.Tags;
import ua.myxazaur.lemonskin.helpers.HungerHelper;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler
{
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

	public static void init()
	{
		CHANNEL.registerMessage(MessageExhaustionSync.class, MessageExhaustionSync.class, 1, Side.CLIENT);
		CHANNEL.registerMessage(MessageSaturationSync.class, MessageSaturationSync.class, 2, Side.CLIENT);
		CHANNEL.registerMessage(MessageThirstExhaustionSync.class, MessageThirstExhaustionSync.class, 3, Side.CLIENT);

		MinecraftForge.EVENT_BUS.register(new SyncHandler());
	}

	private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
	private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();
	private static final Map<UUID, Float> lastThirstExhaustionLevels = new HashMap<>();

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		if (!(event.getEntity() instanceof EntityPlayerMP))
			return;

		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		UUID playerId = player.getUniqueID();

		// Food saturation sync
		Float lastSaturationLevel = lastSaturationLevels.get(playerId);
		if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodStats().getSaturationLevel())
		{
			CHANNEL.sendTo(new MessageSaturationSync(player.getFoodStats().getSaturationLevel()), player);
			lastSaturationLevels.put(playerId, player.getFoodStats().getSaturationLevel());
		}

		// Food exhaustion sync
		float exhaustionLevel = HungerHelper.getExhaustion(player);
		Float lastExhaustionLevel = lastExhaustionLevels.get(playerId);
		if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f)
		{
			CHANNEL.sendTo(new MessageExhaustionSync(exhaustionLevel), player);
			lastExhaustionLevels.put(playerId, exhaustionLevel);
		}

		// Thirst exhaustion sync (SimpleDifficulty)
		if (LemonSkin.hasSimpleDifficulty)
		{
			float thirstExhaustion = ThirstHelper.getExhaustion(player);
			Float lastThirstExhaustion = lastThirstExhaustionLevels.get(playerId);
			if (lastThirstExhaustion == null || Math.abs(lastThirstExhaustion - thirstExhaustion) >= 0.01f)
			{
				CHANNEL.sendTo(new MessageThirstExhaustionSync(thirstExhaustion), player);
				lastThirstExhaustionLevels.put(playerId, thirstExhaustion);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
			return;

		UUID playerId = event.player.getUniqueID();
		lastSaturationLevels.remove(playerId);
		lastExhaustionLevels.remove(playerId);
		lastThirstExhaustionLevels.remove(playerId);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
			return;

		UUID playerId = event.player.getUniqueID();
		lastSaturationLevels.remove(playerId);
		lastExhaustionLevels.remove(playerId);
		lastThirstExhaustionLevels.remove(playerId);
	}
}
package electroblob.wizardry.potion;

import java.util.List;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.entity.construct.EntityDecay;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionDecay extends Potion {
	
	private static final ResourceLocation ICON = new ResourceLocation(Wizardry.MODID, "textures/gui/decay_icon.png");

	public PotionDecay(boolean isBadEffect, int liquidColour) {
		super(isBadEffect, liquidColour);
		// This needs to be here because registerPotionAttributeModifier doesn't like it if the potion has no name yet.
		this.setPotionName("potion.wizardry:decay");
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "85602e0b-4801-4a87-94f3-bf617c97014e", -Constants.DECAY_SLOWNESS_PER_LEVEL, 2);
	}
	
	@Override
	public boolean isReady(int p_76397_1_, int p_76397_2_) {
		// Copied from the vanilla wither effect. It does the timing stuff. 25 is the number of ticks between hits at
		// amplifier 0
		int k = 25 >> p_76397_2_;
		return k > 0 ? p_76397_1_ % k == 0 : true;
	}
	
	@Override
	public void performEffect(EntityLivingBase target, int strength) {
		
		target.attackEntityFrom(DamageSource.wither, 1);
		
		if(target.onGround && target.ticksExisted % Constants.DECAY_SPREAD_INTERVAL == 0){

			List<Entity> entities = target.worldObj.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox());

			boolean flag = true;

			for(Entity entity : entities){
				if(entity instanceof EntityDecay) flag = false;
			}

			if(flag){
				// The victim spreading the decay is the 'caster' here, so that it can actually wear off, otherwise it just gets infected with its own decay and the effect lasts forever.
				target.worldObj.spawnEntityInWorld(new EntityDecay(target.worldObj, target.posX, target.posY, target.posZ, target));
			}
		}
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) {
        mc.renderEngine.bindTexture(ICON);
        WizardryUtilities.drawTexturedRect(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {
		mc.renderEngine.bindTexture(ICON);
		WizardryUtilities.drawTexturedRect(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}

}

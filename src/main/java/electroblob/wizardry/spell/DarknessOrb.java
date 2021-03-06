package electroblob.wizardry.spell;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.projectile.EntityDarknessOrb;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DarknessOrb extends Spell {

	public DarknessOrb() {
		super(Tier.ADVANCED, 20, Element.NECROMANCY, "darkness_orb", SpellType.ATTACK, 20, EnumAction.NONE, false);
	}

	@Override
	public boolean doesSpellRequirePacket(){
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		
		if(!world.isRemote){
			EntityDarknessOrb darknessorb = new EntityDarknessOrb(world, caster, modifiers.get(SpellModifiers.DAMAGE));
			world.spawnEntityInWorld(darknessorb);
		}
		
		WizardryUtilities.playSoundAtPlayer(caster, SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
		caster.swingArm(hand);
		return true;
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers){
		
		if(target != null){
			
			if(!world.isRemote){
				EntityDarknessOrb darknessorb = new EntityDarknessOrb(world, caster, modifiers.get(SpellModifiers.DAMAGE));
				darknessorb.directTowards(target, 0.5f);
				world.spawnEntityInWorld(darknessorb);
			}

			caster.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 1.0F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
			caster.swingArm(hand);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean canBeCastByNPCs(){
		return true;
	}

}

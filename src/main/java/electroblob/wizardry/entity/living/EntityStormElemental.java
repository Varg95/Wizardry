package electroblob.wizardry.entity.living;

import java.util.Collections;
import java.util.List;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WizardryParticleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityStormElemental extends EntitySummonedCreature implements ISpellCaster {
	
	private double AISpeed = 1.0;
    
    private EntityAIAttackSpell spellAttackAI = new EntityAIAttackSpell(this, AISpeed, 15f, 30, 0);
    
	private static final List<Spell> attack = Collections.singletonList(Spells.lightning_disc);

	public EntityStormElemental(World world){
		super(world);
	}

	public EntityStormElemental(World world, double x, double y, double z, EntityLivingBase caster, int lifetime){
		super(world, x, y, z, caster, lifetime);
		// For some reason this can't be in initEntityAI
        this.tasks.addTask(0, this.spellAttackAI);
	}
	
	@Override
	protected void initEntityAI(){

        this.tasks.addTask(1, new EntityAIAttackMelee(this, AISpeed, false));
		this.tasks.addTask(2, new EntityAIWander(this, AISpeed));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 
        		0, false, true, this.getTargetSelector()));

        this.setAIMoveSpeed((float)AISpeed);
	}

	@Override
	public boolean hasRangedAttack() {
		return true;
	}

	@Override
	public List<Spell> getSpells(){
		return attack;
	}

	@Override
	public SpellModifiers getModifiers(){
		return new SpellModifiers();
	}

	@Override
	public Spell getContinuousSpell(){
		return Spells.none;
	}

	@Override
	public void setContinuousSpell(Spell spell){
		// Doesn't use continuous spells.
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(AISpeed);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

    @Override
    protected SoundEvent getAmbientSound(){
        return SoundEvents.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(){
        return SoundEvents.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound(){
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialTicks){
		return 15728880;
	}

	@Override
	public float getBrightness(float partialTicks){
		return 1.0F;
	}

	@Override
	public void onLivingUpdate(){

		if(this.ticksExisted % 120 == 1){
			this.playSound(WizardrySounds.SPELL_LOOP_WIND, 1.0f, 1.0f);
		}

		if (this.rand.nextInt(24) == 0){
			this.playSound(SoundEvents.ENTITY_BLAZE_BURN, 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
		}

		// Slow fall
		if(!this.onGround && this.motionY < 0.0D){
			this.motionY *= 0.6D;
		}

		if(worldObj.isRemote){
			
			for(int i=0; i<2; ++i){
				worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0, 0, 0);
				Wizardry.proxy.spawnParticle(WizardryParticleType.SPARK, worldObj, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0d, 0.0d, 0.0d, 0, 0, 0, 0);
			}

			for(int i=0; i<10; i++){
				float brightness = rand.nextFloat()*0.2f;
				double dy = this.rand.nextDouble() * (double)this.height;
				Wizardry.proxy.spawnParticle(WizardryParticleType.SPARKLE_ROTATING, worldObj, this.posX, this.posY + dy, this.posZ, 0, 0, 0, 20 + rand.nextInt(10), 0, brightness, brightness, false, 0.2f + 0.5f*dy);
			}
		}

		super.onLivingUpdate();
	}
	
	@Override
	public void fall(float distance, float damageMultiplier){
		// Immune to fall damage.
	}
	
	@Override
	public void onStruckByLightning(EntityLightningBolt lightning){
		// Immune to lightning.
	}
}
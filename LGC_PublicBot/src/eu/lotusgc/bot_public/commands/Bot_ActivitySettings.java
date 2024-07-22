//Created by Christopher at 28.03.2024
package eu.lotusgc.bot_public.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class Bot_ActivitySettings extends ListenerAdapter{
	
	static long ownerId = 228145889988837385l;
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Member member = event.getMember();
		if(event.getName().equals("status")) {
			if(member.getIdLong() == ownerId) {
				TextInput state = TextInput.create("mdlState", "Status", TextInputStyle.SHORT)
						.setRequiredRange(3, 16)
						.setPlaceholder("Use either: online, offline, idle, dnd")
						.build();
				
				Modal stateModal = Modal.create("modalState", "Bot Onlinestatus")
						.addComponents(ActionRow.of(state))
						.build();
				event.replyModal(stateModal).queue();
			}else {
				event.deferReply(true).addContent("Nope, you aren't my owner!").queue();
			}
		}else if(event.getName().equals("activity")) {
			if(member.getIdLong() == ownerId) {
				TextInput activityKey = TextInput.create("mdlActKey", "Activity Key", TextInputStyle.SHORT)
						.setRequiredRange(5, 16)
						.setPlaceholder("Use either: watching, playing, listening")
						.build();
				TextInput activityText = TextInput.create("mdlActTxt", "Activity Text", TextInputStyle.PARAGRAPH)
						.setRequiredRange(8, 128)
						.setPlaceholder("The text appended to the activity.")
						.build();
				Modal activityModal = Modal.create("modalActivity", "Bot Activity")
						.addComponents(ActionRow.of(activityKey), ActionRow.of(activityText))
						.build();
				event.replyModal(activityModal).queue();
			}else {
				event.deferReply(true).addContent("Nope, you aren't my owner!").queue();
			}
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		if(event.getModalId().equals("modalState")) {
			
		}else if(event.getModalId().equals("modalActivity")) {
			
		}
	}
}
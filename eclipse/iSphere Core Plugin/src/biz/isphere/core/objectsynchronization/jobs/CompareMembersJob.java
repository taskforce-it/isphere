/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.Messages;
import biz.isphere.core.objectsynchronization.MemberDescription;
import biz.isphere.core.objectsynchronization.SynchronizeMembersEditorInput;

public class CompareMembersJob extends Job implements ICancelableJob {

    private SynchronizeMembersEditorInput input;
    private CompareMembersSharedJobValues sharedValues;
    private ICompareMembersPostrun postRun;

    private SubMonitor subMonitor;

    public CompareMembersJob(SynchronizeMembersEditorInput input, CompareMembersSharedJobValues sharedValues, ICompareMembersPostrun postRun) {
        super(Messages.Job_Loading_source_members);

        this.input = input;
        this.sharedValues = sharedValues;
        this.postRun = postRun;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        subMonitor = SubMonitor.convert(monitor, 4 * 2);

        List<AbstractCompareMembersJob> workerJobs = new LinkedList<AbstractCompareMembersJob>();

        StartCompareMembersJob startMembersJob = new StartCompareMembersJob(subMonitor, sharedValues, input.getLeftObject(), input.getRightObject());
        workerJobs.add(startMembersJob);

        ResolveGenericCompareElementsJob resolveGenericElementsJob = new ResolveGenericCompareElementsJob(subMonitor, sharedValues);
        workerJobs.add(resolveGenericElementsJob);

        LoadCompareMembersJob loadMembersJob = new LoadCompareMembersJob(subMonitor, sharedValues);
        workerJobs.add(loadMembersJob);

        FinishCompareMembersJob finishMembersJob = new FinishCompareMembersJob(subMonitor, sharedValues);
        workerJobs.add(finishMembersJob);

        MemberDescription[] leftMemberDescriptions = new MemberDescription[0];
        MemberDescription[] rightMemberDescriptions = new MemberDescription[0];

        try {

            for (AbstractCompareMembersJob workerJob : workerJobs) {
                workerJob.run();
            }

            leftMemberDescriptions = loadMembersJob.getLeftMembers();
            rightMemberDescriptions = loadMembersJob.getRightMembers();

        } finally {

            finishMembersJob.run();

            subMonitor.done();

            postRun.returnResult(subMonitor.isCanceled(), leftMemberDescriptions, rightMemberDescriptions);
        }

        return Status.OK_STATUS;
    }

    public void cancelOperation() {
        subMonitor.setCanceled(true);
    }
}
